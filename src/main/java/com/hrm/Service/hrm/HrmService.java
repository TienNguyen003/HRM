package com.hrm.Service.hrm;

import com.hrm.Entity.hrm.*;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.repository.hrm.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Nghiệp vụ module HRM (/api/hrm).
 *
 * Toàn bộ dữ liệu được lưu trong DB qua Repository. Seed chỉ đảm nhiệm việc khởi tạo
 * danh mục tham chiếu và (tuỳ cấu hình) dữ liệu mẫu một lần; mọi thao tác đều đi qua
 * service này với validation, transaction và audit log.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HrmService {

    HrmEmployeeRepository employeeRepository;
    HrmAttendanceRepository attendanceRepository;
    HrmLeaveRequestRepository leaveRequestRepository;
    HrmLeaveBalanceRepository leaveBalanceRepository;
    HrmDocumentRepository documentRepository;
    HrmActivityLogRepository activityLogRepository;
    HrmCandidateRepository candidateRepository;
    HrmInterviewRepository interviewRepository;
    HrmPositionRepository positionRepository;
    HrmGoalRepository goalRepository;
    HrmKpiRepository kpiRepository;
    HrmReviewRepository reviewRepository;
    HrmFeedbackRepository feedbackRepository;
    HrmNotificationRepository notificationRepository;
    HrmGrowthRepository growthRepository;
    HrmOvertimeRequestRepository overtimeRepository;
    HrmPayrollRunRepository payrollRunRepository;
    HrmPayslipRepository payslipRepository;
    HrmOnboardingTaskRepository onboardingRepository;
    HrmOffboardingTaskRepository offboardingRepository;
    HrmLeavePolicyRepository leavePolicyRepository;
    HrmShiftScheduleRepository shiftScheduleRepository;
    HrmRosterRepository rosterRepository;
    HrmBenefitRepository benefitRepository;
    HrmBenefitEnrollmentRepository benefitEnrollmentRepository;
    HrmHrRequestRepository hrRequestRepository;
    HrmAnnouncementRepository announcementRepository;
    HrmCompanyEventRepository companyEventRepository;
    HrmReferenceDataRepository referenceDataRepository;
    HrmAuditService auditService;

    // ---------------------------------------------------------------- helpers
    private void notFound(String label) {
        throw new AppException(ErrorCode.HRM_NOT_FOUND, label);
    }

    private void invalidRequest(String message) {
        throw new AppException(ErrorCode.HRM_INVALID_REQUEST, message);
    }

    private String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName() != null && !"anonymousUser".equals(auth.getName())
                ? auth.getName() : "system";
    }

    private void audit(String action, String entity, String entityId) {
        auditService.record(action, entity, entityId, currentActor());
    }

    private String firstNonBlank(Object... values) {
        if (values == null) return null;
        for (Object value : values) {
            if (value != null && !String.valueOf(value).isBlank()) return String.valueOf(value).trim();
        }
        return null;
    }

    private String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String nvl(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String normalizeId(Object value) {
        return value == null ? "" : String.valueOf(value).trim().toUpperCase();
    }

    private int intOf(Object value, int fallback) {
        try {
            if (value instanceof Number n) return n.intValue();
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return fallback;
        }
    }

    private double doubleOf(Object value, double fallback) {
        try {
            if (value instanceof Number n) return n.doubleValue();
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            return fallback;
        }
    }

    private boolean matches(Object value, String query) {
        return String.valueOf(value == null ? "" : value).toLowerCase()
                .contains(String.valueOf(query == null ? "" : query).trim().toLowerCase());
    }

    private List<String> uniqueStr(List<Map<String, Object>> collection, String key) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (Map<String, Object> item : collection) {
            if (item.get(key) != null) set.add(String.valueOf(item.get(key)));
        }
        return new ArrayList<>(set);
    }

    private List<Map<String, Object>> sortBy(List<Map<String, Object>> collection, String field, String direction) {
        if (field == null || field.isBlank()) return new ArrayList<>(collection);
        List<Map<String, Object>> copy = new ArrayList<>(collection);
        boolean desc = "desc".equalsIgnoreCase(direction);
        copy.sort((left, right) -> {
            Object a = left.get(field);
            Object b = right.get(field);
            int cmp;
            if (a instanceof Number && b instanceof Number)
                cmp = Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
            else
                cmp = String.valueOf(a == null ? "" : a).compareToIgnoreCase(String.valueOf(b == null ? "" : b));
            return desc ? -cmp : cmp;
        });
        return copy;
    }

    private Map<String, Object> paginate(List<Map<String, Object>> collection, int page, int pageSize) {
        int safeSize = pageSize <= 0 ? 10 : pageSize;
        int total = collection.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / safeSize));
        int safePage = Math.min(Math.max(1, page <= 0 ? 1 : page), totalPages);
        int start = (safePage - 1) * safeSize;
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = start; i < Math.min(start + safeSize, total); i++) items.add(collection.get(i));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", items);
        result.put("page", safePage);
        result.put("pageSize", safeSize);
        result.put("total", total);
        result.put("totalPages", totalPages);
        result.put("from", total == 0 ? 0 : start + 1);
        result.put("to", Math.min(start + safeSize, total));
        return result;
    }

    private String nextId(List<String> existingIds, String prefix, int width, int start) {
        int highest = start - 1;
        for (String id : existingIds) {
            String numeric = id == null ? "" : id.replaceAll("\\D", "");
            try {
                int n = Integer.parseInt(numeric);
                if (n > highest) highest = n;
            } catch (Exception ignored) {
            }
        }
        return prefix + "-" + String.format("%0" + width + "d", highest + 1);
    }

    private String nextEmployeeId() {
        return nextId(employeeRepository.findAll().stream().map(HrmEmployee::getId).toList(), "EMP", 5, 132);
    }

    private String nextIdFor(List<String> ids, String prefix, int width, int start) {
        return nextId(ids, prefix, width, start);
    }

    private String initialsOf(String name) {
        String[] parts = String.valueOf(name).trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++)
            if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        return sb.length() == 0 ? "NA" : sb.toString();
    }

    private String formatDisplayDate(String iso) {
        if (iso == null || iso.isBlank()) return "";
        try {
            return LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return iso;
        }
    }

    private void requireIsoDate(String value, String field) {
        if (value == null || value.isBlank()) invalidRequest(field + " không được để trống.");
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            invalidRequest(field + " phải có định dạng YYYY-MM-DD.");
        }
    }

    private List<String> referenceValues(String category) {
        return referenceDataRepository.findByCategoryOrderBySortOrderAsc(category).stream()
                .map(HrmReferenceData::getCode).toList();
    }

    private void requireIn(String value, String category, String label) {
        List<String> allowed = referenceValues(category);
        if (value != null && !allowed.isEmpty() && !allowed.contains(value))
            invalidRequest(label + " không hợp lệ: " + value);
    }

    private HrmEmployee findEmployeeEntity(String id) {
        return employeeRepository.findById(id).orElse(null);
    }

    private HrmEmployee requireEmployee(String id) {
        HrmEmployee e = findEmployeeEntity(normalizeId(id));
        if (e == null) notFound("Nhân viên");
        return e;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFilterOptions() {
        Map<String, Object> r = new LinkedHashMap<>();
        for (String key : List.of("departments", "locations", "employmentTypes", "employeeStatuses",
                "attendanceStatuses", "leaveTypes", "leaveStatuses", "recruitmentStages", "goalPriorities")) {
            r.put(key, referenceValues(key));
        }
        return r;
    }

    private <T extends HrmAttributesAware> T fillAttributes(Map<String, Object> source, T entity) {
        Set<String> known = new HashSet<>(HrmJson.mapper().convertValue(entity,
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}).keySet());
        known.add("attributes");
        Map<String, Object> attrs = new LinkedHashMap<>();
        source.forEach((k, v) -> {
            if (!known.contains(k)) attrs.put(k, v);
        });
        entity.setAttributes(attrs);
        return entity;
    }

    @Transactional
    protected void pushActivity(String employeeId, String type, String detail, String icon, String tone) {
        if (employeeId == null) return;
        String id = nextId(activityLogRepository.findAll().stream().map(HrmActivityLog::getId).toList(), "ACT", 3, 1);
        activityLogRepository.save(new HrmActivityLog(id, employeeId, type, detail, "Just now", icon, tone));
    }

    // ---------------------------------------------------------------- employee
    @Transactional(readOnly = true)
    public Map<String, Object> getEmployeeDirectory(String search, String department, String status, String position,
                                                    String employmentType, String location, String sort,
                                                    String sortDirection, Integer page, Integer pageSize) {
        List<Map<String, Object>> all = HrmJson.toMaps(employeeRepository.findAll());
        List<Map<String, Object>> filtered = all.stream().filter(e -> {
            boolean ms = (search == null || search.isBlank()) || matches(e.get("name"), search)
                    || matches(e.get("id"), search) || matches(e.get("email"), search)
                    || matches(e.get("position"), search) || matches(e.get("phone"), search);
            boolean md = (department == null || department.isBlank()) || Objects.equals(str(e.get("department")), department);
            boolean mst = (status == null || status.isBlank()) || Objects.equals(str(e.get("status")), status);
            boolean mp = (position == null || position.isBlank()) || Objects.equals(str(e.get("position")), position);
            boolean me = (employmentType == null || employmentType.isBlank()) || Objects.equals(str(e.get("employmentType")), employmentType);
            boolean ml = (location == null || location.isBlank()) || Objects.equals(str(e.get("location")), location);
            return ms && md && mst && mp && me && ml;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered,
                sort == null || sort.isBlank() ? "name" : sort, sortDirection == null ? "asc" : sortDirection);
        Map<String, Object> r = new LinkedHashMap<>(paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize));
        r.put("allItems", sorted);
        r.put("departments", uniqueStr(all, "department"));
        r.put("positions", uniqueStr(all, "position"));
        r.put("locations", uniqueStr(all, "location"));
        r.put("employmentTypes", uniqueStr(all, "employmentType"));
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEmployeeSummary() {
        List<Map<String, Object>> all = HrmJson.toMaps(employeeRepository.findAll());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", all.size());
        r.put("active", all.stream().filter(e -> "Active".equals(e.get("status"))).count());
        r.put("onLeave", all.stream().filter(e -> "On Leave".equals(e.get("status"))).count());
        r.put("inactive", all.stream().filter(e -> "Inactive".equals(e.get("status"))).count());
        r.put("departments", uniqueStr(all, "department").size());
        return r;
    }

    @Transactional(readOnly = true)
    public boolean isEmployeeIdAvailable(String id) {
        return id == null || id.isBlank() || !employeeRepository.existsById(normalizeId(id));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEmployeeById(String id) {
        HrmEmployee emp = requireEmployee(id);
        Map<String, Object> r = HrmJson.toMap(emp);
        String empId = emp.getId();
        r.put("attendance", HrmJson.toMaps(attendanceRepository.findAllByEmployee_Id(empId)));
        r.put("leave", HrmJson.toMaps(leaveRequestRepository.findAllByEmployee_Id(empId)));
        List<HrmLeaveBalance> balances = leaveBalanceRepository.findByEmployee_Id(empId);
        r.put("balance", balances.isEmpty() ? null : HrmJson.toMap(balances.get(0)));
        r.put("documents", HrmJson.toMaps(documentRepository.findAllByEmployee_Id(empId)));
        r.put("activity", HrmJson.toMaps(activityLogRepository.findAllByEmployeeId(empId)));
        r.put("directReports", employeeRepository.findAll().stream()
                .filter(x -> emp.getName().equalsIgnoreCase(nvl(x.getManager())))
                .map(HrmEmployee::getId).toList());
        return r;
    }

    private void applyEmployeePayload(HrmEmployee e, Map<String, Object> payload, boolean create) {
        if (payload == null) payload = Map.of();
        String first = create || payload.containsKey("firstName") ? nvl(payload.get("firstName")) : nvl(e.getFirstName());
        String last = create || payload.containsKey("lastName") ? nvl(payload.get("lastName")) : nvl(e.getLastName());
        boolean nameChanged = create || payload.containsKey("firstName") || payload.containsKey("lastName") || payload.containsKey("name");

        String status = firstNonBlank(payload.get("employeeStatus"), payload.get("status"));
        if (create || status != null) e.setStatus(status == null ? "Active" : status);
        String joinIso = firstNonBlank(payload.get("joinDate"), payload.get("joiningDate"), payload.get("joinedDateIso"));
        if (create || joinIso != null) {
            requireIsoDate(joinIso == null ? LocalDate.now().toString() : joinIso, "joinDate");
            String iso = joinIso == null ? LocalDate.now().toString() : joinIso;
            e.setJoinedDateIso(iso);
            e.setJoinedDate(formatDisplayDate(iso));
        }

        Map<String, Object> ec = payload.get("emergencyContact") instanceof Map<?, ?> m
                ? (Map<String, Object>) m : Map.of();
        if (create || !ec.isEmpty() || payload.containsKey("emergencyName")) {
            e.setEmergencyName(str(firstNonBlank(ec.get("name"), payload.get("emergencyName"))));
            e.setEmergencyRelation(str(firstNonBlank(ec.get("relation"), payload.get("emergencyRelation"))));
            e.setEmergencyPhone(str(firstNonBlank(ec.get("phone"), payload.get("emergencyPhone"))));
        }

        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            String k = entry.getKey();
            if (Set.of("employeeId", "id", "firstName", "lastName", "name", "password", "confirmPassword",
                    "employeeStatus", "joinDate", "joiningDate", "joinedDateIso", "emergencyContact",
                    "emergencyName", "emergencyRelation", "emergencyPhone").contains(k)) continue;
            switch (k) {
                case "position" -> e.setPosition(str(entry.getValue()));
                case "department" -> e.setDepartment(str(entry.getValue()));
                case "manager" -> e.setManager(str(entry.getValue()));
                case "employmentType" -> e.setEmploymentType(str(entry.getValue()));
                case "email" -> e.setEmail(str(entry.getValue()));
                case "phone" -> e.setPhone(str(entry.getValue()));
                case "dateOfBirth" -> e.setDateOfBirth(str(entry.getValue()));
                case "address" -> e.setAddress(str(entry.getValue()));
                case "location" -> e.setLocation(str(entry.getValue()));
                case "idCard" -> e.setIdCard(str(entry.getValue()));
                case "bankAccount" -> e.setBankAccount(str(entry.getValue()));
                case "probationEndDate" -> e.setProbationEndDate(str(entry.getValue()));
                case "avatar" -> e.setAvatar(str(entry.getValue()));
                case "attendanceRate" -> e.setAttendanceRate(intOf(entry.getValue(), 0));
                case "leaveBalance" -> e.setLeaveBalance(intOf(entry.getValue(), 20));
                case "goals" -> e.setGoals(intOf(entry.getValue(), 0));
                default -> {
                }
            }
        }
        if (create) {
            e.setFirstName(first);
            e.setLastName(last);
        } else {
            if (payload.containsKey("firstName")) e.setFirstName(first);
            if (payload.containsKey("lastName")) e.setLastName(last);
        }
        if (nameChanged) {
            String name = payload.containsKey("name") && !payload.containsKey("firstName") && !payload.containsKey("lastName")
                    ? nvl(payload.get("name")) : (first + " " + last).trim();
            if (name.isBlank()) name = nvl(e.getName());
            if (name.isBlank()) name = "New Employee";
            e.setName(name);
            e.setInitials(initialsOf(name));
        }
        if (create) {
            if (e.getPosition() == null || e.getPosition().isBlank()) e.setPosition("Team Member");
            if (e.getDepartment() == null || e.getDepartment().isBlank()) e.setDepartment("Unassigned");
            if (e.getManager() == null || e.getManager().isBlank()) e.setManager("Not assigned");
            if (e.getEmploymentType() == null || e.getEmploymentType().isBlank()) e.setEmploymentType("Full-time");
            if (e.getLocation() == null || e.getLocation().isBlank()) e.setLocation("Ho Chi Minh City");
            e.setAttendanceRate(0);
            e.setLeaveBalance(20);
            e.setGoals(0);
        }
        requireIn(e.getStatus(), "employeeStatuses", "Trạng thái nhân viên");
        requireIn(e.getEmploymentType(), "employmentTypes", "Loại hình làm việc");
    }

    @Transactional
    public Map<String, Object> createEmployee(Map<String, Object> payload) {
        String requested = normalizeId(payload.get("employeeId"));
        String id = requested.matches("EMP-\\d{5}") && !employeeRepository.existsById(requested)
                ? requested : nextEmployeeId();
        HrmEmployee e = new HrmEmployee();
        e.setId(id);
        applyEmployeePayload(e, payload, true);
        employeeRepository.save(e);

        String today = LocalDate.now().toString();
        HrmAttendance att = new HrmAttendance();
        att.setId("ATT-" + id + "-" + today);
        att.setEmployee(e);
        att.setEmployeeName(e.getName());
        att.setDepartment(e.getDepartment());
        att.setDate(today);
        att.setDateLabel(formatDisplayDate(today));
        att.setWeekday(LocalDate.now().getDayOfWeek().toString().substring(0, 3));
        att.setCheckIn("08:30");
        att.setCheckOut("--");
        att.setHours("In progress");
        att.setLateMinutes(0);
        att.setStatus("Present");
        attendanceRepository.save(att);

        HrmLeaveBalance balance = newLeaveBalance(e);
        leaveBalanceRepository.save(balance);
        pushActivity(id, "Employee created", e.getName() + " was added to " + e.getDepartment() + ".",
                "fas fa-user-plus", "success");
        audit("CREATE", "Employee", id);
        return HrmJson.toMap(e);
    }

    private HrmLeaveBalance newLeaveBalance(HrmEmployee e) {
        HrmLeaveBalance b = new HrmLeaveBalance();
        b.setId(e.getId());
        b.setEmployee(e);
        b.setEmployeeName(e.getName());
        b.setDepartment(e.getDepartment());
        b.setAnnualTotal(20); b.setAnnualUsed(0); b.setAnnualPending(0);
        b.setSickTotal(12); b.setSickUsed(0); b.setSickPending(0);
        b.setUnpaidTotal(0); b.setUnpaidUsed(0); b.setUnpaidPending(0);
        return b;
    }

    @Transactional
    public Map<String, Object> updateEmployee(String id, Map<String, Object> payload) {
        HrmEmployee e = requireEmployee(id);
        String normalized = normalizeId(id);
        if (payload != null && payload.containsKey("status")) e.setStatus(str(payload.get("status")));
        applyEmployeePayload(e, payload, false);
        employeeRepository.save(e);
        pushActivity(normalized, "Profile updated", e.getName() + "'s profile was updated.", "fas fa-user-pen", "info");
        audit("UPDATE", "Employee", normalized);
        return HrmJson.toMap(e);
    }

    @Transactional
    public Map<String, Object> deleteEmployee(String id) {
        HrmEmployee e = requireEmployee(id);
        String empId = e.getId();
        attendanceRepository.deleteAllByEmployee_Id(empId);
        documentRepository.deleteAllByEmployee_Id(empId);
        leaveRequestRepository.deleteAllByEmployee_Id(empId);
        leaveBalanceRepository.deleteByEmployee_Id(empId);
        employeeRepository.delete(e);
        audit("DELETE", "Employee", empId);
        return HrmJson.toMap(e);
    }

    // ---------------------------------------------------------------- organization
    @Transactional(readOnly = true)
    public Map<String, Object> getOrganizationOverview() {
        List<Map<String, Object>> all = HrmJson.toMaps(employeeRepository.findAll());
        List<String> depts = uniqueStr(all, "department");
        List<Map<String, Object>> byDep = new ArrayList<>();
        for (String name : depts) {
            List<Map<String, Object>> members = all.stream().filter(e -> name.equals(e.get("department"))).toList();
            Map<String, Object> lead = members.stream()
                    .filter(m -> all.stream().anyMatch(x -> nvl(x.get("manager")).equals(m.get("name"))))
                    .findFirst().orElse(members.isEmpty() ? null : members.get(0));
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("name", name);
            d.put("manager", lead == null ? "Not assigned" : lead.get("name"));
            d.put("managerPosition", lead == null ? "" : lead.get("position"));
            d.put("managerId", lead == null ? null : lead.get("id"));
            d.put("headcount", members.size());
            d.put("locations", uniqueStr(members, "location"));
            List<Map<String, Object>> mems = new ArrayList<>();
            for (Map<String, Object> m : members) {
                Map<String, Object> mm = new LinkedHashMap<>();
                for (String k : List.of("id", "name", "position", "status", "initials")) mm.put(k, m.get(k));
                mems.add(mm);
            }
            d.put("members", mems);
            byDep.add(d);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("departments", byDep);
        r.put("total", all.size());
        r.put("locations", uniqueStr(all, "location"));
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDepartmentDetail(String name) {
        String department = name == null ? "" : name.trim();
        if (department.isBlank()) invalidRequest("Tên phòng ban không được để trống.");
        List<Map<String, Object>> all = HrmJson.toMaps(employeeRepository.findAll());
        List<Map<String, Object>> members = all.stream().filter(e -> department.equals(e.get("department"))).toList();
        if (members.isEmpty()) notFound("Phòng ban");

        Map<String, Object> lead = members.stream()
                .filter(m -> all.stream().anyMatch(x -> nvl(x.get("manager")).equals(m.get("name"))))
                .findFirst().orElse(members.get(0));
        Map<String, Integer> posCount = countBy(members, "position");
        Map<String, Integer> empTypeCount = countBy(members, "employmentType");
        Map<String, Integer> statusCount = countBy(members, "status");
        List<Map<String, Object>> positions = toLabelValue(posCount, true);
        List<Map<String, Object>> empTypes = toLabelValue(empTypeCount, false);
        List<Map<String, Object>> statuses = toLabelValue(statusCount, false);

        Set<String> memberIds = members.stream().map(m -> str(m.get("id"))).collect(Collectors.toSet());
        String today = LocalDate.now().toString();
        List<Map<String, Object>> todayRecords = HrmJson.toMaps(attendanceRepository.findAll()).stream()
                .filter(r -> memberIds.contains(str(r.get("employeeId"))) && today.equals(r.get("date"))).toList();
        List<Map<String, Object>> deptLeave = HrmJson.toMaps(leaveRequestRepository.findAll()).stream()
                .filter(r -> memberIds.contains(str(r.get("employeeId")))).toList();

        Map<String, Object> attendanceToday = new LinkedHashMap<>();
        for (String s : List.of("Present", "Late", "Working From Home", "On Leave", "Absent")) {
            String key = switch (s) {
                case "Working From Home" -> "remote";
                case "On Leave" -> "onLeave";
                default -> s.toLowerCase(Locale.ROOT);
            };
            attendanceToday.put(key, todayRecords.stream().filter(r -> s.equals(r.get("status"))).count());
        }
        double avgTenure = members.stream().mapToDouble(m -> {
            try {
                String iso = str(m.getOrDefault("joinedDateIso", m.get("joinedDate")));
                LocalDate joined = LocalDate.parse(iso);
                return java.time.temporal.ChronoUnit.DAYS.between(joined, LocalDate.now()) / 365.0;
            } catch (Exception ex) {
                return 0;
            }
        }).average().orElse(0);

        Map<String, Object> manager = null;
        if (lead != null) {
            manager = new LinkedHashMap<>();
            for (String k : List.of("id", "name", "position", "email", "phone", "initials", "status")) manager.put(k, lead.get(k));
            manager.put("joiningDate", lead.get("joinedDate"));
        }
        List<Map<String, Object>> mems = new ArrayList<>();
        for (Map<String, Object> m : members) {
            Map<String, Object> mm = new LinkedHashMap<>();
            for (String k : List.of("id", "name", "initials", "position", "status", "employmentType",
                    "email", "phone", "location", "manager")) mm.put(k, m.get(k));
            String iso = str(m.getOrDefault("joinedDateIso", m.get("joinedDate")));
            mm.put("joiningDate", formatDisplayDate(iso));
            mems.add(mm);
        }

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("name", department);
        r.put("manager", manager);
        r.put("headcount", members.size());
        r.put("locations", uniqueStr(members, "location"));
        r.put("positions", positions);
        r.put("employmentTypes", empTypes);
        r.put("statuses", statuses);
        r.put("averageTenureYears", Math.round(avgTenure * 10.0) / 10.0);
        r.put("attendanceToday", attendanceToday);
        r.put("leavePending", deptLeave.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        r.put("leaveApprovedDays", deptLeave.stream().filter(x -> "Approved".equals(x.get("status")))
                .mapToInt(x -> intOf(x.get("days"), 0)).sum());
        r.put("members", mems);
        return r;
    }

    private Map<String, Integer> countBy(List<Map<String, Object>> list, String key) {
        Map<String, Integer> acc = new LinkedHashMap<>();
        for (Map<String, Object> item : list) {
            String k = str(item.get(key));
            acc.put(k, acc.getOrDefault(k, 0) + 1);
        }
        return acc;
    }

    private List<Map<String, Object>> toLabelValue(Map<String, Integer> counts, boolean sortDesc) {
        List<Map<String, Object>> result = new ArrayList<>();
        counts.forEach((k, v) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("label", k);
            m.put("value", v);
            result.add(m);
        });
        if (sortDesc) result.sort((a, b) -> Integer.compare((int) b.get("value"), (int) a.get("value")));
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrganizationChart() {
        List<HrmEmployee> all = employeeRepository.findAll();
        HrmEmployee rootEmp = all.stream().filter(e -> "Board of Directors".equals(e.getManager()))
                .findFirst().orElse(all.isEmpty() ? null : all.get(0));
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("root", buildOrgNode(rootEmp, new HashSet<>(), all));
        r.put("total", all.size());
        return r;
    }

    private Map<String, Object> buildOrgNode(HrmEmployee employee, Set<String> visited, List<HrmEmployee> all) {
        if (employee == null || visited.contains(employee.getId())) return null;
        Set<String> next = new HashSet<>(visited);
        next.add(employee.getId());
        Map<String, Object> node = new LinkedHashMap<>();
        for (String k : List.of("id", "name", "position", "department", "manager", "initials"))
            node.put(k, HrmJson.toMap(employee).get(k));
        List<Map<String, Object>> reports = new ArrayList<>();
        for (HrmEmployee rep : all) {
            if (employee.getName().equalsIgnoreCase(nvl(rep.getManager()))) {
                Map<String, Object> child = buildOrgNode(rep, next, all);
                if (child != null) reports.add(child);
            }
        }
        node.put("reports", reports);
        return node;
    }

    // ---------------------------------------------------------------- attendance
    @Transactional(readOnly = true)
    public Map<String, Object> getAttendanceRecords(String date, String department, String employeeId, String status,
                                                    String sort, String sortDirection, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = HrmJson.toMaps(attendanceRepository.findAll()).stream().filter(r -> {
            boolean md = (date == null || date.isBlank()) || Objects.equals(str(r.get("date")), date);
            boolean mdep = (department == null || department.isBlank()) || Objects.equals(str(r.get("department")), department);
            boolean me = (employeeId == null || employeeId.isBlank()) || Objects.equals(str(r.get("employeeId")), employeeId);
            boolean ms = (status == null || status.isBlank()) || Objects.equals(str(r.get("status")), status);
            return md && mdep && me && ms;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, sort, sortDirection);
        Map<String, Object> r = new LinkedHashMap<>(paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize));
        r.put("allItems", sorted);
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAttendanceSummary() {
        String today = LocalDate.now().toString();
        List<Map<String, Object>> all = HrmJson.toMaps(attendanceRepository.findAll());
        List<Map<String, Object>> todayRecords = all.stream().filter(r -> today.equals(r.get("date"))).toList();
        List<Map<String, Object>> weekdayRecords = all.stream()
                .filter(r -> !List.of("Sat", "Sun").contains(r.get("weekday"))).toList();
        Map<String, Integer> countBy = new LinkedHashMap<>();
        for (Map<String, Object> r : weekdayRecords) {
            String st = str(r.get("status"));
            countBy.put(st, countBy.getOrDefault(st, 0) + 1);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("date", today);
        r.put("present", todayRecords.stream().filter(x -> "Present".equals(x.get("status"))).count());
        r.put("late", todayRecords.stream().filter(x -> "Late".equals(x.get("status"))).count());
        r.put("absent", todayRecords.stream().filter(x -> "Absent".equals(x.get("status"))).count());
        r.put("onLeave", todayRecords.stream().filter(x -> "On Leave".equals(x.get("status"))).count());
        r.put("remote", todayRecords.stream().filter(x -> "Working From Home".equals(x.get("status"))).count());
        r.put("totalEmployees", employeeRepository.count());
        r.put("statusBreakdown", countBy);
        r.put("lateEmployees", all.stream().filter(x -> "Late".equals(x.get("status"))).limit(6).toList());
        List<Map<String, Object>> deptBreakdown = new ArrayList<>();
        List<Map<String, Object>> employees = HrmJson.toMaps(employeeRepository.findAll());
        for (String name : uniqueStr(employees, "department")) {
            List<Map<String, Object>> rows = all.stream().filter(x -> name.equals(x.get("department"))).toList();
            long present = rows.stream().filter(x -> "Present".equals(x.get("status")) || "Working From Home".equals(x.get("status"))).count();
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("name", name);
            d.put("total", rows.size());
            d.put("present", (int) present);
            d.put("rate", rows.isEmpty() ? 0 : Math.round((double) present / rows.size() * 100));
            deptBreakdown.add(d);
        }
        r.put("departmentBreakdown", deptBreakdown);
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAttendanceCalendar(String month, String department) {
        String m = (month == null || month.isBlank()) ? "2026-09" : month;
        List<Map<String, Object>> rows = HrmJson.toMaps(attendanceRepository.findAll()).stream()
                .filter(r -> (department == null || department.isBlank() || department.equals(r.get("department")))
                        && str(r.get("date")).startsWith(m)).toList();
        Map<String, Map<String, Integer>> byDate = new LinkedHashMap<>();
        for (Map<String, Object> rec : rows) {
            String d = str(rec.get("date"));
            byDate.computeIfAbsent(d, k -> new LinkedHashMap<>(Map.of(
                    "present", 0, "late", 0, "absent", 0, "onLeave", 0, "remote", 0)));
            Map<String, Integer> acc = byDate.get(d);
            String st = str(rec.get("status"));
            switch (st) {
                case "Present" -> acc.merge("present", 1, Integer::sum);
                case "Late" -> acc.merge("late", 1, Integer::sum);
                case "Absent" -> acc.merge("absent", 1, Integer::sum);
                case "On Leave" -> acc.merge("onLeave", 1, Integer::sum);
                case "Working From Home" -> acc.merge("remote", 1, Integer::sum);
                default -> {
                }
            }
        }
        String[] parts = m.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthIndex = Integer.parseInt(parts[1]);
        int daysInMonth = YearMonth.of(year, monthIndex).lengthOfMonth();
        List<Map<String, Object>> cells = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            String date = String.format("%s-%02d", m, i);
            Map<String, Object> cell = new LinkedHashMap<>();
            cell.put("date", date);
            cell.put("day", i);
            cell.putAll(byDate.getOrDefault(date, Map.of("present", 0, "late", 0, "absent", 0, "onLeave", 0, "remote", 0)));
            cells.add(cell);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("month", m);
        r.put("cells", cells);
        r.put("weekdays", List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        return r;
    }

    @Transactional
    public Map<String, Object> checkIn(String employeeId) {
        HrmEmployee employee = requireEmployee(employeeId);
        LocalDate now = LocalDate.now();
        String iso = now.toString();
        String stamp = String.format("%02d:%02d", java.time.LocalDateTime.now().getHour(), java.time.LocalDateTime.now().getMinute());
        String id = "ATT-" + employee.getId() + "-" + iso;
        HrmAttendance record = attendanceRepository.findById(id).orElse(new HrmAttendance());
        record.setId(id);
        record.setEmployee(employee);
        record.setEmployeeName(employee.getName());
        record.setDepartment(employee.getDepartment());
        record.setDate(iso);
        record.setDateLabel(formatDisplayDate(iso));
        record.setWeekday(now.getDayOfWeek().toString().substring(0, 3));
        record.setCheckIn(stamp);
        record.setCheckOut("--");
        record.setHours("In progress");
        record.setLateMinutes(0);
        record.setStatus("Present");
        attendanceRepository.save(record);
        pushActivity(employee.getId(), "Check in", employee.getName() + " checked in at " + stamp + ".",
                "fas fa-right-to-bracket", "success");
        audit("CHECK_IN", "Attendance", id);
        return HrmJson.toMap(record);
    }

    @Transactional
    public Map<String, Object> checkOut(String employeeId) {
        HrmEmployee employee = requireEmployee(employeeId);
        HrmAttendance record = attendanceRepository.findAllByEmployee_Id(employee.getId()).stream()
                .filter(x -> "--".equals(x.getCheckOut())).findFirst().orElse(null);
        if (record == null) notFound("Bản ghi chấm công");
        record.setCheckOut(String.format("%02d:%02d", java.time.LocalDateTime.now().getHour(), java.time.LocalDateTime.now().getMinute()));
        record.setHours("Recorded");
        attendanceRepository.save(record);
        audit("CHECK_OUT", "Attendance", record.getId());
        return HrmJson.toMap(record);
    }

    // ---------------------------------------------------------------- leave
    @Transactional(readOnly = true)
    public Map<String, Object> getLeaveRequests(String search, String status, String type, String employeeId,
                                                String sort, String sortDirection, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = HrmJson.toMaps(leaveRequestRepository.findAll()).stream().filter(item -> {
            boolean ms = (status == null || status.isBlank()) || Objects.equals(str(item.get("status")), status);
            boolean me = (employeeId == null || employeeId.isBlank()) || Objects.equals(str(item.get("employeeId")), employeeId);
            boolean mt = (type == null || type.isBlank()) || Objects.equals(str(item.get("type")), type);
            boolean mq = (search == null || search.isBlank()) || matches(item.get("employeeName"), search)
                    || matches(item.get("id"), search) || matches(item.get("reason"), search);
            return ms && me && mt && mq;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, sort, sortDirection);
        Map<String, Object> r = new LinkedHashMap<>(paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize));
        r.put("allItems", sorted);
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getLeaveSummary() {
        List<Map<String, Object>> all = HrmJson.toMaps(leaveRequestRepository.findAll());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", all.size());
        r.put("pending", all.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        r.put("approved", all.stream().filter(x -> "Approved".equals(x.get("status"))).count());
        r.put("rejected", all.stream().filter(x -> "Rejected".equals(x.get("status"))).count());
        r.put("cancelled", all.stream().filter(x -> "Cancelled".equals(x.get("status"))).count());
        r.put("totalDays", all.stream().mapToInt(x -> intOf(x.get("days"), 0)).sum());
        String todayIso = LocalDate.now().toString();
        r.put("onLeaveToday", all.stream().filter(x -> "Approved".equals(x.get("status"))
                && str(x.get("start")).compareTo(todayIso) <= 0 && str(x.get("end")).compareTo(todayIso) >= 0).count());
        List<Map<String, Object>> byType = new ArrayList<>();
        for (String t : referenceValues("leaveTypes")) {
            int v = (int) all.stream().filter(x -> t.equals(x.get("type"))).count();
            if (v > 0) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("name", t);
                m.put("value", v);
                byType.add(m);
            }
        }
        r.put("byType", byType);
        return r;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLeaveBalances(String employeeId) {
        List<HrmLeaveBalance> balances = (employeeId == null || employeeId.isBlank())
                ? leaveBalanceRepository.findAll() : leaveBalanceRepository.findByEmployee_Id(employeeId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (HrmLeaveBalance b : balances) {
            Map<String, Object> m = HrmJson.toMap(b);
            int total = 0, used = 0, pending = 0;
            for (String bucket : List.of("annual", "sick", "unpaid")) {
                if (m.get(bucket) instanceof Map<?, ?> bm) {
                    total += intOf(bm.get("total"), 0);
                    used += intOf(bm.get("used"), 0);
                    pending += intOf(bm.get("pending"), 0);
                }
            }
            m.put("balance", Math.max(0, total - used - pending));
            m.put("used", used);
            m.put("pending", pending);
            m.put("total", total);
            result.add(m);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getLeaveById(String id) {
        HrmLeaveRequest item = leaveRequestRepository.findById(id).orElse(null);
        if (item == null) notFound("Yêu cầu nghỉ phép");
        Map<String, Object> r = HrmJson.toMap(item);
        List<Map<String, Object>> timeline = new ArrayList<>();
        timeline.add(timeline("Employee submitted request", item.getSubmitted(), "done"));
        timeline.add(timeline("Manager review", "Pending".equals(item.getStatus()) ? "Awaiting decision" : item.getApprover(),
                "Pending".equals(item.getStatus()) ? "current" : "done"));
        timeline.add(timeline("Pending".equals(item.getStatus()) ? "Final decision" : item.getStatus(),
                "Pending".equals(item.getStatus()) ? "--" : item.getApprover(),
                "Pending".equals(item.getStatus()) ? "upcoming" : "done"));
        r.put("timeline", timeline);
        return r;
    }

    private Map<String, Object> timeline(String label, Object date, String state) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label", label);
        m.put("date", date);
        m.put("state", state);
        return m;
    }

    @Transactional
    public Map<String, Object> createLeaveRequest(Map<String, Object> payload) {
        HrmEmployee employee = requireEmployee(str(payload.get("employeeId")));
        String type = nvl(payload.get("type"));
        if (type.isBlank()) type = "Annual Leave";
        requireIn(type, "leaveTypes", "Loại nghỉ phép");
        String start = str(payload.get("start"));
        String end = str(payload.get("end"));
        requireIsoDate(start, "start");
        requireIsoDate(end, "end");
        if (start.compareTo(end) > 0) invalidRequest("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        int days = intOf(payload.get("days"), 0);
        if (days <= 0) invalidRequest("Số ngày nghỉ phải lớn hơn 0.");

        String id = nextIdFor(leaveRequestRepository.findAll().stream().map(HrmLeaveRequest::getId).toList(), "LV", 3, 1);
        HrmLeaveRequest item = new HrmLeaveRequest();
        item.setId(id);
        item.setEmployee(employee);
        item.setEmployeeName(employee.getName());
        item.setType(type);
        item.setStart(start);
        item.setEnd(end);
        item.setDays(days);
        item.setStatus("Pending");
        item.setApprover(employee.getManager());
        item.setSubmitted(LocalDate.now().toString());
        item.setReason(nvl(payload.get("reason")));
        leaveRequestRepository.save(item);

        adjustBalance(employee, type, days, true, false);
        pushActivity(employee.getId(), "Leave requested", days + " day(s) of " + type + " requested.",
                "fas fa-calendar-plus", "info");
        audit("CREATE", "LeaveRequest", id);
        return HrmJson.toMap(item);
    }

    @Transactional
    public Map<String, Object> updateLeaveStatus(String id, String status, String approver) {
        requireIn(status, "leaveStatuses", "Trạng thái nghỉ phép");
        HrmLeaveRequest item = leaveRequestRepository.findById(id).orElse(null);
        if (item == null) notFound("Yêu cầu nghỉ phép");
        String previous = item.getStatus();
        item.setStatus(status);
        if (approver != null && !approver.isBlank()) item.setApprover(approver);
        leaveRequestRepository.save(item);

        if ("Pending".equals(previous) && !"Pending".equals(status)) {
            adjustBalance(item.getEmployee(), item.getType(), item.getDays(), false, "Approved".equals(status));
        }
        pushActivity(item.getEmployeeId(), "Leave " + status.toLowerCase(),
                item.getDays() + " day(s) of " + item.getType() + " was " + status.toLowerCase() + ".",
                "fas fa-calendar-check", "Approved".equals(status) ? "success" : "danger");
        audit("UPDATE_STATUS", "LeaveRequest", id);
        return HrmJson.toMap(item);
    }

    private void adjustBalance(HrmEmployee employee, String type, int days, boolean pendingUp, boolean approve) {
        if (employee == null) return;
        HrmLeaveBalance b = leaveBalanceRepository.findByEmployee_Id(employee.getId()).stream().findFirst().orElse(null);
        if (b == null) {
            b = newLeaveBalance(employee);
        }
        String bucket = "Sick Leave".equals(type) ? "sick" : "Unpaid Leave".equals(type) ? "unpaid" : "annual";
        int pending = switch (bucket) {
            case "sick" -> b.getSickPending();
            case "unpaid" -> b.getUnpaidPending();
            default -> b.getAnnualPending();
        };
        int used = switch (bucket) {
            case "sick" -> b.getSickUsed();
            case "unpaid" -> b.getUnpaidUsed();
            default -> b.getAnnualUsed();
        };
        if (pendingUp) {
            pending += days;
        } else {
            pending = Math.max(0, pending - days);
            if (approve) used += days;
        }
        switch (bucket) {
            case "sick" -> { b.setSickPending(pending); b.setSickUsed(used); }
            case "unpaid" -> { b.setUnpaidPending(pending); b.setUnpaidUsed(used); }
            default -> { b.setAnnualPending(pending); b.setAnnualUsed(used); }
        }
        leaveBalanceRepository.save(b);
    }

    // ---------------------------------------------------------------- recruitment
    @Transactional(readOnly = true)
    public Map<String, Object> getRecruitmentOverview() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("candidates", HrmJson.toMaps(candidateRepository.findAll()));
        r.put("positions", HrmJson.toMaps(positionRepository.findAll()));
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRecruitmentSummary() {
        List<Map<String, Object>> candidates = HrmJson.toMaps(candidateRepository.findAll());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("openPositions", positionRepository.findAll().stream().filter(p -> "Open".equals(p.getStatus())).count());
        r.put("totalCandidates", candidates.size());
        r.put("interviewing", candidates.stream().filter(c -> "Interview".equals(c.get("stage"))).count());
        r.put("offers", candidates.stream().filter(c -> "Offer".equals(c.get("stage"))).count());
        r.put("hired", candidates.stream().filter(c -> "Hired".equals(c.get("stage"))).count());
        List<Map<String, Object>> pipeline = new ArrayList<>();
        for (String s : referenceValues("recruitmentStages")) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", s);
            m.put("value", candidates.stream().filter(c -> s.equals(c.get("stage"))).count());
            pipeline.add(m);
        }
        r.put("pipeline", pipeline);
        r.put("interviews", HrmJson.toMaps(interviewRepository.findAll()).stream().limit(5).toList());
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCandidateById(String id) {
        HrmCandidate candidate = candidateRepository.findById(id).orElse(null);
        if (candidate == null) notFound("Ứng viên");
        Map<String, Object> r = HrmJson.toMap(candidate);
        r.put("interviews", HrmJson.toMaps(interviewRepository.findAllByCandidate_Id(id)));
        return r;
    }

    @Transactional
    public Map<String, Object> createCandidate(Map<String, Object> payload) {
        HrmCandidate c = HrmJson.fromMap(payload, HrmCandidate.class);
        c.setId(nextIdFor(candidateRepository.findAll().stream().map(HrmCandidate::getId).toList(), "CAN", 4, 1001));
        if (c.getName() == null || c.getName().isBlank()) invalidRequest("Tên ứng viên không được để trống.");
        c.setStage(c.getStage() == null ? "Applied" : c.getStage());
        requireIn(c.getStage(), "recruitmentStages", "Giai đoạn tuyển dụng");
        if (c.getApplied() == null || c.getApplied().isBlank()) c.setApplied(LocalDate.now().toString());
        if (c.getInitials() == null || c.getInitials().isBlank()) c.setInitials(initialsOf(c.getName()));
        c.setAttributes(payload);
        candidateRepository.save(c);
        audit("CREATE", "Candidate", c.getId());
        return HrmJson.toMap(c);
    }

    @Transactional
    public Map<String, Object> updateCandidate(String id, Map<String, Object> payload) {
        HrmCandidate c = candidateRepository.findById(id).orElse(null);
        if (c == null) notFound("Ứng viên");
        HrmCandidate updated = HrmJson.fromMap(payload, HrmCandidate.class);
        copyCandidateFields(updated, c);
        c.getAttributes().putAll(payload);
        candidateRepository.save(c);
        audit("UPDATE", "Candidate", id);
        return HrmJson.toMap(c);
    }

    private void copyCandidateFields(HrmCandidate from, HrmCandidate to) {
        if (from.getName() != null) to.setName(from.getName());
        if (from.getInitials() != null) to.setInitials(from.getInitials());
        if (from.getRole() != null) to.setRole(from.getRole());
        if (from.getStage() != null) to.setStage(from.getStage());
        if (from.getApplied() != null) to.setApplied(from.getApplied());
        if (from.getLocation() != null) to.setLocation(from.getLocation());
        if (from.getRating() != null) to.setRating(from.getRating());
        if (from.getEmail() != null) to.setEmail(from.getEmail());
        if (from.getPhone() != null) to.setPhone(from.getPhone());
        if (from.getExperience() != null) to.setExperience(from.getExperience());
        if (from.getSource() != null) to.setSource(from.getSource());
    }

    @Transactional
    public Map<String, Object> deleteCandidate(String id) {
        HrmCandidate c = candidateRepository.findById(id).orElse(null);
        if (c == null) notFound("Ứng viên");
        interviewRepository.findAllByCandidate_Id(id).forEach(interviewRepository::delete);
        candidateRepository.delete(c);
        audit("DELETE", "Candidate", id);
        return HrmJson.toMap(c);
    }

    @Transactional
    public Map<String, Object> updateCandidateStage(String id, String stage) {
        requireIn(stage, "recruitmentStages", "Giai đoạn tuyển dụng");
        HrmCandidate c = candidateRepository.findById(id).orElse(null);
        if (c == null) notFound("Ứng viên");
        c.setStage(stage);
        List<Map<String, Object>> history = c.getStageHistory() == null ? new ArrayList<>() : new ArrayList<>(c.getStageHistory());
        Map<String, Object> h = new LinkedHashMap<>();
        h.put("stage", stage);
        h.put("date", LocalDate.now().toString());
        history.add(h);
        c.setStageHistory(history);
        candidateRepository.save(c);
        audit("UPDATE_STAGE", "Candidate", id);
        return HrmJson.toMap(c);
    }

    @Transactional
    public Map<String, Object> scheduleInterview(Map<String, Object> payload) {
        String candidateId = str(payload.get("candidateId"));
        HrmCandidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) notFound("Ứng viên");
        String date = str(payload.get("date"));
        requireIsoDate(date, "date");
        HrmInterview i = new HrmInterview();
        i.setId(nextIdFor(interviewRepository.findAll().stream().map(HrmInterview::getId).toList(), "ITV", 2, 1));
        i.setCandidate(candidate);
        i.setCandidateName(candidate.getName());
        i.setRole(candidate.getRole());
        i.setDate(date);
        i.setTime(nvl(payload.getOrDefault("time", "10:00")));
        i.setDuration(intOf(payload.get("duration"), 60));
        i.setMode(nvl(payload.getOrDefault("mode", "Google Meet")));
        i.setInterviewer(nvl(payload.getOrDefault("interviewer", "Unassigned")));
        i.setStatus("Scheduled");
        interviewRepository.save(i);
        audit("CREATE", "Interview", i.getId());
        return HrmJson.toMap(i);
    }

    @Transactional
    public Map<String, Object> updateInterview(String id, Map<String, Object> payload) {
        HrmInterview i = interviewRepository.findById(id).orElse(null);
        if (i == null) notFound("Lịch phỏng vấn");
        HrmInterview u = HrmJson.fromMap(payload, HrmInterview.class);
        if (u.getDate() != null) i.setDate(u.getDate());
        if (u.getTime() != null) i.setTime(u.getTime());
        if (u.getMode() != null) i.setMode(u.getMode());
        if (u.getInterviewer() != null) i.setInterviewer(u.getInterviewer());
        if (u.getDuration() != 0) i.setDuration(u.getDuration());
        if (payload.get("status") != null) i.setStatus(str(payload.get("status")));
        interviewRepository.save(i);
        audit("UPDATE", "Interview", id);
        return HrmJson.toMap(i);
    }

    @Transactional
    public Map<String, Object> cancelInterview(String id) {
        HrmInterview i = interviewRepository.findById(id).orElse(null);
        if (i == null) notFound("Lịch phỏng vấn");
        i.setStatus("Cancelled");
        interviewRepository.save(i);
        audit("CANCEL", "Interview", id);
        return HrmJson.toMap(i);
    }

    @Transactional
    public Map<String, Object> createPosition(Map<String, Object> payload) {
        HrmPosition p = new HrmPosition();
        p.setId(nextIdFor(positionRepository.findAll().stream().map(HrmPosition::getId).toList(), "POS", 2, 1));
        p.setTitle(nvl(payload.get("title")));
        if (p.getTitle().isBlank()) invalidRequest("Tiêu đề vị trí không được để trống.");
        p.setDepartment(nvl(payload.get("department")));
        p.setLocation(nvl(payload.getOrDefault("location", "Ho Chi Minh City")));
        p.setType(nvl(payload.getOrDefault("type", "Full-time")));
        p.setStatus("Open");
        p.setCandidates(0);
        p.setCreated(LocalDate.now().toString());
        p.setPostedDate(LocalDate.now().toString());
        p.setOpenings(intOf(payload.get("openings"), 1));
        p.setDescription(nvl(payload.get("description")));
        positionRepository.save(p);
        audit("CREATE", "Position", p.getId());
        return HrmJson.toMap(p);
    }

    @Transactional
    public Map<String, Object> updatePosition(String id, Map<String, Object> payload) {
        HrmPosition p = positionRepository.findById(id).orElse(null);
        if (p == null) notFound("Vị trí tuyển dụng");
        HrmPosition u = HrmJson.fromMap(payload, HrmPosition.class);
        if (u.getTitle() != null) p.setTitle(u.getTitle());
        if (u.getDepartment() != null) p.setDepartment(u.getDepartment());
        if (u.getLocation() != null) p.setLocation(u.getLocation());
        if (u.getType() != null) p.setType(u.getType());
        if (u.getStatus() != null) p.setStatus(u.getStatus());
        if (u.getOpenings() != 0) p.setOpenings(u.getOpenings());
        if (u.getDescription() != null) p.setDescription(u.getDescription());
        positionRepository.save(p);
        audit("UPDATE", "Position", id);
        return HrmJson.toMap(p);
    }

    @Transactional
    public Map<String, Object> closePosition(String id) {
        HrmPosition p = positionRepository.findById(id).orElse(null);
        if (p == null) notFound("Vị trí tuyển dụng");
        p.setStatus("Closed");
        positionRepository.save(p);
        audit("CLOSE", "Position", id);
        return HrmJson.toMap(p);
    }

    // ---------------------------------------------------------------- performance
    @Transactional(readOnly = true)
    public Map<String, Object> getPerformanceOverview() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("goals", HrmJson.toMaps(goalRepository.findAll()));
        r.put("reviews", HrmJson.toMaps(reviewRepository.findAll()));
        r.put("feedback", HrmJson.toMaps(feedbackRepository.findAll()));
        r.put("kpis", HrmJson.toMaps(kpiRepository.findAll()));
        r.put("employees", HrmJson.toMaps(employeeRepository.findAll()).stream()
                .filter(e -> "Active".equals(e.get("status"))).toList());
        return r;
    }

    @Transactional
    public Map<String, Object> createGoal(Map<String, Object> payload) {
        HrmGoal g = new HrmGoal();
        g.setId(nextIdFor(goalRepository.findAll().stream().map(HrmGoal::getId).toList(), "GOAL", 2, 1));
        g.setTitle(nvl(payload.get("title")));
        if (g.getTitle().isBlank()) invalidRequest("Tiêu đề mục tiêu không được để trống.");
        g.setOwner(nvl(payload.get("owner")));
        g.setDepartment(nvl(payload.get("department")));
        int progress = Math.min(100, Math.max(0, intOf(payload.get("progress"), 0)));
        g.setProgress(progress);
        String due = str(payload.get("due"));
        g.setDueIso(due == null || due.isBlank() ? "2026-12-31" : due);
        g.setDue(due == null || due.isBlank() ? "31 Dec 2026" : formatDisplayDate(due));
        g.setPriority(nvl(payload.getOrDefault("priority", "Medium")));
        requireIn(g.getPriority(), "goalPriorities", "Độ ưu tiên");
        g.setMetric(nvl(payload.getOrDefault("metric", "Progress")));
        g.setTarget(nvl(payload.getOrDefault("target", "100%")));
        g.setStatus(goalStatus(progress));
        goalRepository.save(g);
        audit("CREATE", "Goal", g.getId());
        return HrmJson.toMap(g);
    }

    private String goalStatus(int progress) {
        return progress >= 100 ? "Completed" : (progress < 40 ? "At Risk" : "On Track");
    }

    @Transactional
    public Map<String, Object> updateGoalProgress(String id, Object progress) {
        HrmGoal g = goalRepository.findById(id).orElse(null);
        if (g == null) notFound("Mục tiêu");
        int p = Math.min(100, Math.max(0, intOf(progress, 0)));
        g.setProgress(p);
        g.setStatus(goalStatus(p));
        goalRepository.save(g);
        audit("UPDATE_PROGRESS", "Goal", id);
        return HrmJson.toMap(g);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReviewById(String id) {
        HrmReview r = reviewRepository.findById(id).orElse(null);
        if (r == null) notFound("Đánh giá");
        return HrmJson.toMap(r);
    }

    @Transactional
    public Map<String, Object> updateReview(String id, Map<String, Object> payload) {
        HrmReview r = reviewRepository.findById(id).orElse(null);
        if (r == null) notFound("Đánh giá");
        HrmReview u = HrmJson.fromMap(payload, HrmReview.class);
        copyReviewFields(u, r);
        r.getAttributes().putAll(payload);
        reviewRepository.save(r);
        audit("UPDATE", "Review", id);
        return HrmJson.toMap(r);
    }

    private void copyReviewFields(HrmReview from, HrmReview to) {
        if (from.getEmployee() != null) to.setEmployee(from.getEmployee());
        if (from.getEmployeeId() != null) to.setEmployeeId(from.getEmployeeId());
        if (from.getCycle() != null) to.setCycle(from.getCycle());
        if (from.getReviewer() != null) to.setReviewer(from.getReviewer());
        if (from.getScore() != null) to.setScore(from.getScore());
        if (from.getStatus() != null) to.setStatus(from.getStatus());
        if (from.getDate() != null) to.setDate(from.getDate());
        if (from.getStrengths() != null) to.setStrengths(from.getStrengths());
        if (from.getImprovements() != null) to.setImprovements(from.getImprovements());
    }

    @Transactional
    public Map<String, Object> completeReview(String id, Map<String, Object> payload) {
        HrmReview r = reviewRepository.findById(id).orElse(null);
        if (r == null) notFound("Đánh giá");
        HrmReview u = HrmJson.fromMap(payload, HrmReview.class);
        copyReviewFields(u, r);
        r.getAttributes().putAll(payload);
        r.setStatus("Completed");
        r.setDate(LocalDate.now().toString());
        reviewRepository.save(r);
        audit("COMPLETE", "Review", id);
        return HrmJson.toMap(r);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getKpis() {
        return HrmJson.toMaps(kpiRepository.findAll());
    }

    @Transactional
    public Map<String, Object> createKpi(Map<String, Object> payload) {
        HrmKpi k = HrmJson.fromMap(payload, HrmKpi.class);
        k.setId(nextIdFor(kpiRepository.findAll().stream().map(HrmKpi::getId).toList(), "KPI", 3, 1));
        if (k.getName() == null || k.getName().isBlank()) invalidRequest("Tên KPI không được để trống.");
        if (k.getStatus() == null || k.getStatus().isBlank()) k.setStatus("Active");
        fillAttributes(payload, k);
        kpiRepository.save(k);
        audit("CREATE", "Kpi", k.getId());
        return HrmJson.toMap(k);
    }

    @Transactional
    public Map<String, Object> updateKpi(String id, Map<String, Object> payload) {
        HrmKpi k = kpiRepository.findById(id).orElse(null);
        if (k == null) notFound("KPI");
        HrmKpi u = HrmJson.fromMap(payload, HrmKpi.class);
        if (u.getName() != null) k.setName(u.getName());
        if (u.getOwner() != null) k.setOwner(u.getOwner());
        if (u.getDepartment() != null) k.setDepartment(u.getDepartment());
        if (u.getCurrent() != null) k.setCurrent(u.getCurrent());
        if (u.getTarget() != null) k.setTarget(u.getTarget());
        if (u.getUnit() != null) k.setUnit(u.getUnit());
        if (u.getTrend() != null) k.setTrend(u.getTrend());
        if (u.getStatus() != null) k.setStatus(u.getStatus());
        k.getAttributes().putAll(payload);
        kpiRepository.save(k);
        audit("UPDATE", "Kpi", id);
        return HrmJson.toMap(k);
    }

    @Transactional
    public Map<String, Object> deleteKpi(String id) {
        HrmKpi k = kpiRepository.findById(id).orElse(null);
        if (k == null) notFound("KPI");
        kpiRepository.delete(k);
        audit("DELETE", "Kpi", id);
        return HrmJson.toMap(k);
    }

    @Transactional
    public Map<String, Object> createFeedback(Map<String, Object> payload) {
        HrmFeedback f = HrmJson.fromMap(payload, HrmFeedback.class);
        f.setId(nextIdFor(feedbackRepository.findAll().stream().map(HrmFeedback::getId).toList(), "FB", 3, 1));
        f.setCreated(LocalDate.now().toString());
        fillAttributes(payload, f);
        feedbackRepository.save(f);
        audit("CREATE", "Feedback", f.getId());
        return HrmJson.toMap(f);
    }

    @Transactional
    public Map<String, Object> updateFeedback(String id, Map<String, Object> payload) {
        HrmFeedback f = feedbackRepository.findById(id).orElse(null);
        if (f == null) notFound("Nhận xét");
        HrmFeedback u = HrmJson.fromMap(payload, HrmFeedback.class);
        if (u.getTone() != null) f.setTone(u.getTone());
        if (u.getText() != null) f.setText(u.getText());
        if (u.getAuthor() != null) f.setAuthor(u.getAuthor());
        if (u.getSubject() != null) f.setSubject(u.getSubject());
        f.getAttributes().putAll(payload);
        feedbackRepository.save(f);
        audit("UPDATE", "Feedback", id);
        return HrmJson.toMap(f);
    }

    // ---------------------------------------------------------------- documents
    @Transactional
    public Map<String, Object> addEmployeeDocument(String employeeId, Map<String, Object> payload) {
        HrmEmployee employee = requireEmployee(employeeId);
        HrmDocument d = new HrmDocument();
        d.setId(nextIdFor(documentRepository.findAll().stream().map(HrmDocument::getId).toList(), "DOC", 2, 1));
        d.setEmployee(employee);
        d.setName(nvl(payload.getOrDefault("name", "Document.pdf")));
        d.setType(nvl(payload.getOrDefault("type", "Other")));
        d.setSize(nvl(payload.getOrDefault("size", "1.0 MB")));
        d.setUpdated("Just now");
        d.setExpires(str(payload.get("expires")));
        d.setUrl(str(payload.get("url")));
        d.setFileName(str(payload.get("fileName")));
        d.setContentType(str(payload.get("contentType")));
        documentRepository.save(d);
        pushActivity(employee.getId(), "Document uploaded", d.getName() + " was added to the profile.",
                "fas fa-file-arrow-up", "primary");
        audit("CREATE", "Document", d.getId());
        return HrmJson.toMap(d);
    }

    @Transactional
    public Map<String, Object> removeEmployeeDocument(String documentId) {
        HrmDocument d = documentRepository.findById(documentId).orElse(null);
        if (d == null) notFound("Tài liệu");
        documentRepository.delete(d);
        pushActivity(d.getEmployeeId(), "Document removed", d.getName() + " was removed from the profile.",
                "fas fa-file-circle-xmark", "warning");
        audit("DELETE", "Document", documentId);
        return HrmJson.toMap(d);
    }

    // ---------------------------------------------------------------- notifications
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getNotifications() {
        return HrmJson.toMaps(notificationRepository.findAll());
    }

    @Transactional(readOnly = true)
    public long getUnreadNotificationCount() {
        return notificationRepository.findAll().stream().filter(HrmNotification::isUnread).count();
    }

    @Transactional
    public Map<String, Object> markNotificationRead(String id) {
        HrmNotification n = notificationRepository.findById(id).orElse(null);
        if (n == null) notFound("Thông báo");
        n.setUnread(false);
        notificationRepository.save(n);
        return HrmJson.toMap(n);
    }

    @Transactional
    public List<Map<String, Object>> markAllNotificationsRead() {
        List<HrmNotification> all = notificationRepository.findAll();
        all.forEach(n -> n.setUnread(false));
        notificationRepository.saveAll(all);
        return HrmJson.toMaps(all);
    }

    // ---------------------------------------------------------------- dashboard / reports
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardOverview() {
        String today = LocalDate.now().toString();
        List<Map<String, Object>> employees = HrmJson.toMaps(employeeRepository.findAll());
        List<Map<String, Object>> attendance = HrmJson.toMaps(attendanceRepository.findAll());
        List<Map<String, Object>> leaves = HrmJson.toMaps(leaveRequestRepository.findAll());
        List<Map<String, Object>> reviews = HrmJson.toMaps(reviewRepository.findAll());
        List<Map<String, Object>> candidates = HrmJson.toMaps(candidateRepository.findAll());
        List<Map<String, Object>> positions = HrmJson.toMaps(positionRepository.findAll());
        List<Map<String, Object>> todayRecords = attendance.stream().filter(r -> today.equals(r.get("date"))).toList();
        Map<String, Object> metrics = new LinkedHashMap<>();
        String monthStart = LocalDate.now().withDayOfMonth(1).toString();
        metrics.put("totalEmployees", employees.size());
        metrics.put("activeEmployees", employees.stream().filter(e -> "Active".equals(e.get("status"))).count());
        metrics.put("newEmployees", employees.stream().filter(e -> str(e.get("joinedDateIso")).compareTo(monthStart) >= 0).count());
        metrics.put("onLeave", employees.stream().filter(e -> "On Leave".equals(e.get("status"))).count());
        metrics.put("absentToday", todayRecords.stream().filter(x -> "Absent".equals(x.get("status"))).count());
        metrics.put("openPositions", positions.stream().filter(p -> "Open".equals(p.get("status"))).count());
        metrics.put("pendingRequests", leaves.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        metrics.put("pendingReviews", reviews.stream().filter(x -> "Pending".equals(x.get("status"))).count());

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("metrics", metrics);
        r.put("growth", HrmJson.toMaps(growthRepository.findAll()));
        r.put("departmentDistribution", distribution(employees, "department"));
        List<Map<String, Object>> attBreak = new ArrayList<>();
        for (String s : referenceValues("attendanceStatuses")) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", s);
            m.put("value", attendance.stream().filter(x -> s.equals(x.get("status"))).count());
            attBreak.add(m);
        }
        r.put("attendanceBreakdown", attBreak);
        List<Map<String, Object>> leaveStats = new ArrayList<>();
        for (String s : referenceValues("leaveStatuses")) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", s);
            m.put("value", leaves.stream().filter(x -> s.equals(x.get("status"))).count());
            leaveStats.add(m);
        }
        r.put("leaveStats", leaveStats);
        List<Map<String, Object>> hiring = new ArrayList<>();
        for (String s : referenceValues("recruitmentStages")) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", s);
            m.put("value", candidates.stream().filter(c -> s.equals(c.get("stage"))).count());
            hiring.add(m);
        }
        r.put("hiringPipeline", hiring);
        r.put("recentActivity", HrmJson.toMaps(activityLogRepository.findAll()).stream().limit(6).toList());
        r.put("upcomingLeave", leaves.stream().filter(x -> "Pending".equals(x.get("status")) || "Approved".equals(x.get("status")))
                .limit(5).toList());
        return r;
    }

    private List<Map<String, Object>> distribution(List<Map<String, Object>> source, String key) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String name : uniqueStr(source, key)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", name);
            m.put("value", source.stream().filter(e -> name.equals(e.get(key))).count());
            result.add(m);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReportsOverview() {
        List<Map<String, Object>> employees = HrmJson.toMaps(employeeRepository.findAll());
        List<Map<String, Object>> attendance = HrmJson.toMaps(attendanceRepository.findAll());
        List<Map<String, Object>> leaves = HrmJson.toMaps(leaveRequestRepository.findAll());
        List<Map<String, Object>> goals = HrmJson.toMaps(goalRepository.findAll());
        List<Map<String, Object>> reviews = HrmJson.toMaps(reviewRepository.findAll());
        List<Map<String, Object>> kpis = HrmJson.toMaps(kpiRepository.findAll());
        List<Map<String, Object>> candidates = HrmJson.toMaps(candidateRepository.findAll());
        List<Map<String, Object>> positions = HrmJson.toMaps(positionRepository.findAll());

        List<Map<String, Object>> lateByDepartment = new ArrayList<>();
        for (String name : uniqueStr(employees, "department")) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", name);
            m.put("value", attendance.stream().filter(r -> {
                Map<String, Object> emp = employees.stream().filter(e -> str(r.get("employeeId")).equals(e.get("id"))).findFirst().orElse(null);
                return emp != null && name.equals(emp.get("department")) && "Late".equals(r.get("status"));
            }).count());
            lateByDepartment.add(m);
        }
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalEmployees", employees.size());
        metrics.put("activeEmployees", employees.stream().filter(e -> "Active".equals(e.get("status"))).count());
        metrics.put("onLeave", employees.stream().filter(e -> "On Leave".equals(e.get("status"))).count());
        metrics.put("inactive", employees.stream().filter(e -> "Inactive".equals(e.get("status"))).count());
        metrics.put("openPositions", positions.stream().filter(p -> "Open".equals(p.get("status"))).count());
        metrics.put("totalCandidates", candidates.size());
        metrics.put("hiredCandidates", candidates.stream().filter(c -> "Hired".equals(c.get("stage"))).count());
        metrics.put("pendingLeave", leaves.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        metrics.put("approvedLeaveDays", leaves.stream().filter(x -> "Approved".equals(x.get("status"))).mapToInt(x -> intOf(x.get("days"), 0)).sum());
        metrics.put("pendingReviews", reviews.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        metrics.put("activeGoals", goals.stream().filter(g -> !"Completed".equals(g.get("status"))).count());
        metrics.put("completedGoals", goals.stream().filter(g -> "Completed".equals(g.get("status"))).count());

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("metrics", metrics);
        r.put("growth", HrmJson.toMaps(growthRepository.findAll()));
        r.put("departmentDistribution", distribution(employees, "department"));
        r.put("locationDistribution", distribution(employees, "location"));
        r.put("employmentTypeDistribution", distribution(employees, "employmentType"));
        r.put("statusDistribution", distribution(employees, "status"));
        r.put("goalStatusDistribution", distribution(goals, "status"));
        r.put("reviewStatusDistribution", distribution(reviews, "status"));
        r.put("kpiStatusDistribution", distribution(kpis, "status"));
        r.put("attendanceBreakdown", breakdown(attendance, "status", "attendanceStatuses"));
        r.put("leaveStats", breakdown(leaves, "status", "leaveStatuses"));
        r.put("hiringPipeline", distribution(candidates, "stage"));
        r.put("lateByDepartment", lateByDepartment);
        return r;
    }

    private List<Map<String, Object>> breakdown(List<Map<String, Object>> source, String key, String category) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String s : referenceValues(category)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", s);
            m.put("value", source.stream().filter(x -> s.equals(x.get(key))).count());
            result.add(m);
        }
        return result;
    }

    // ---------------------------------------------------------------- overtime
    @Transactional(readOnly = true)
    public Map<String, Object> getOvertime(String employeeId, String department, String status, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = HrmJson.toMaps(overtimeRepository.findAll()).stream().filter(r -> {
            boolean me = (employeeId == null || employeeId.isBlank()) || Objects.equals(str(r.get("employeeId")), employeeId);
            boolean md = (department == null || department.isBlank()) || Objects.equals(str(r.get("department")), department);
            boolean ms = (status == null || status.isBlank()) || Objects.equals(str(r.get("status")), status);
            return me && md && ms;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "date", "desc");
        Map<String, Object> r = new LinkedHashMap<>(paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize));
        r.put("allItems", sorted);
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOvertimeSummary() {
        List<Map<String, Object>> all = HrmJson.toMaps(overtimeRepository.findAll());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", all.size());
        r.put("pending", all.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        r.put("approved", all.stream().filter(x -> "Approved".equals(x.get("status"))).count());
        r.put("rejected", all.stream().filter(x -> "Rejected".equals(x.get("status"))).count());
        r.put("totalHours", all.stream().mapToDouble(x -> doubleOf(x.get("hours"), 0)).sum());
        return r;
    }

    @Transactional
    public Map<String, Object> createOvertime(Map<String, Object> payload) {
        HrmEmployee employee = findEmployeeEntity(normalizeId(payload.get("employeeId")));
        double hours = doubleOf(payload.get("hours"), 0);
        if (hours <= 0) invalidRequest("Số giờ tăng ca phải lớn hơn 0.");
        HrmOvertimeRequest o = new HrmOvertimeRequest();
        o.setId(nextIdFor(overtimeRepository.findAll().stream().map(HrmOvertimeRequest::getId).toList(), "OT", 3, 1));
        o.setEmployee(employee);
        o.setEmployeeName(employee == null ? nvl(payload.get("employeeName")) : employee.getName());
        o.setDepartment(employee == null ? nvl(payload.get("department")) : employee.getDepartment());
        o.setDate(str(payload.getOrDefault("date", LocalDate.now().toString())));
        requireIsoDate(o.getDate(), "date");
        o.setHours(hours);
        o.setReason(nvl(payload.get("reason")));
        o.setStatus("Pending");
        o.setApprover(employee == null ? nvl(payload.get("approver")) : employee.getManager());
        o.setSubmitted(LocalDate.now().toString());
        overtimeRepository.save(o);
        audit("CREATE", "OvertimeRequest", o.getId());
        return HrmJson.toMap(o);
    }

    @Transactional
    public Map<String, Object> updateOvertimeStatus(String id, String status) {
        requireIn(status, "overtimeStatuses", "Trạng thái tăng ca");
        HrmOvertimeRequest o = overtimeRepository.findById(id).orElse(null);
        if (o == null) notFound("Yêu cầu tăng ca");
        o.setStatus(status);
        overtimeRepository.save(o);
        audit("UPDATE_STATUS", "OvertimeRequest", id);
        return HrmJson.toMap(o);
    }

    // ---------------------------------------------------------------- payroll & payslips
    @Transactional(readOnly = true)
    public Map<String, Object> getPayrollRuns(String month, String department, String status, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = HrmJson.toMaps(payrollRunRepository.findAll()).stream().filter(r -> {
            boolean mm = (month == null || month.isBlank()) || Objects.equals(str(r.get("month")), month);
            boolean md = (department == null || department.isBlank()) || Objects.equals(str(r.get("department")), department);
            boolean ms = (status == null || status.isBlank()) || Objects.equals(str(r.get("status")), status);
            return mm && md && ms;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "month", "desc");
        Map<String, Object> r = new LinkedHashMap<>(paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize));
        r.put("allItems", sorted);
        return r;
    }

    @Transactional
    public Map<String, Object> createPayrollRun(Map<String, Object> payload) {
        String month = str(payload.get("month"));
        if (month == null || !month.matches("\\d{4}-\\d{2}")) invalidRequest("Tháng bảng lương phải có dạng YYYY-MM.");
        if (payrollRunRepository.findAll().stream().anyMatch(p -> month.equals(p.getMonth())
                && Objects.equals(nvl(p.getDepartment()), nvl(payload.getOrDefault("department", "All")))))
            invalidRequest("Đợt lương cho tháng/phòng ban này đã tồn tại.");
        HrmPayrollRun p = new HrmPayrollRun();
        p.setId(nextIdFor(payrollRunRepository.findAll().stream().map(HrmPayrollRun::getId).toList(), "PR", 3, 1));
        p.setMonth(month);
        p.setDepartment(nvl(payload.getOrDefault("department", "All")));
        p.setAmount(doubleOf(payload.get("amount"), 0));
        p.setStatus("Draft");
        p.setCreated(LocalDate.now().toString());
        payrollRunRepository.save(p);
        audit("CREATE", "PayrollRun", p.getId());
        return HrmJson.toMap(p);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPayrollRunById(String id) {
        HrmPayrollRun p = payrollRunRepository.findById(id).orElse(null);
        if (p == null) notFound("Bảng lương");
        return HrmJson.toMap(p);
    }

    @Transactional
    public Map<String, Object> updatePayrollRunStatus(String id, String status) {
        requireIn(status, "payrollStatuses", "Trạng thái bảng lương");
        HrmPayrollRun p = payrollRunRepository.findById(id).orElse(null);
        if (p == null) notFound("Bảng lương");
        p.setStatus(status);
        payrollRunRepository.save(p);
        if ("Approved".equals(status)) generatePayslips(p);
        audit("UPDATE_STATUS", "PayrollRun", id);
        return HrmJson.toMap(p);
    }

    private void generatePayslips(HrmPayrollRun run) {
        for (HrmEmployee e : employeeRepository.findAll()) {
            String payslipId = run.getId() + "-" + e.getId();
            if (payslipRepository.existsById(payslipId)) continue;
            double basic = Math.max(0, run.getAmount() == null ? 0 : run.getAmount());
            HrmPayslip slip = new HrmPayslip();
            slip.setId(payslipId);
            slip.setEmployee(e);
            slip.setEmployeeName(e.getName());
            slip.setMonth(run.getMonth());
            slip.setBasic(basic);
            slip.setAllowances(0.0);
            slip.setDeductions(0.0);
            slip.setNet(basic);
            slip.setStatus("Generated");
            slip.setCreated(LocalDate.now().toString());
            payslipRepository.save(slip);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPayslips(String employeeId, String month, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = HrmJson.toMaps(payslipRepository.findAll()).stream().filter(r -> {
            boolean me = (employeeId == null || employeeId.isBlank()) || Objects.equals(str(r.get("employeeId")), employeeId);
            boolean mm = (month == null || month.isBlank()) || Objects.equals(str(r.get("month")), month);
            return me && mm;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "month", "desc");
        Map<String, Object> r = new LinkedHashMap<>(paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize));
        r.put("allItems", sorted);
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPayslipById(String id) {
        HrmPayslip p = payslipRepository.findById(id).orElse(null);
        if (p == null) notFound("Phiếu lương");
        return HrmJson.toMap(p);
    }

    // ---------------------------------------------------------------- onboarding / offboarding
    @Transactional(readOnly = true)
    public Map<String, Object> getOnboarding(String employeeId, String status, Integer page, Integer pageSize) {
        return pagedTasks(HrmJson.toMaps(onboardingRepository.findAll()), employeeId, status, page, pageSize);
    }

    @Transactional
    public Map<String, Object> createOnboarding(Map<String, Object> payload) {
        HrmEmployee employee = findEmployeeEntity(normalizeId(payload.get("employeeId")));
        HrmOnboardingTask t = new HrmOnboardingTask();
        t.setId(nextIdFor(onboardingRepository.findAll().stream().map(HrmOnboardingTask::getId).toList(), "ONB", 3, 1));
        t.setEmployee(employee);
        t.setEmployeeName(employee == null ? nvl(payload.get("employeeName")) : employee.getName());
        t.setTask(nvl(payload.get("task")));
        if (t.getTask().isBlank()) invalidRequest("Tên nhiệm vụ không được để trống.");
        t.setOwner(nvl(payload.get("owner")));
        t.setDueDate(str(payload.get("dueDate")));
        t.setStatus("Pending");
        t.setCreated(LocalDate.now().toString());
        onboardingRepository.save(t);
        audit("CREATE", "OnboardingTask", t.getId());
        return HrmJson.toMap(t);
    }

    @Transactional
    public Map<String, Object> updateOnboardingStatus(String id, String status) {
        requireIn(status, "taskStatuses", "Trạng thái nhiệm vụ");
        HrmOnboardingTask t = onboardingRepository.findById(id).orElse(null);
        if (t == null) notFound("Nhiệm vụ onboarding");
        t.setStatus(status);
        onboardingRepository.save(t);
        audit("UPDATE_STATUS", "OnboardingTask", id);
        return HrmJson.toMap(t);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOffboarding(String employeeId, String status, Integer page, Integer pageSize) {
        return pagedTasks(HrmJson.toMaps(offboardingRepository.findAll()), employeeId, status, page, pageSize);
    }

    @Transactional
    public Map<String, Object> createOffboarding(Map<String, Object> payload) {
        HrmEmployee employee = findEmployeeEntity(normalizeId(payload.get("employeeId")));
        HrmOffboardingTask t = new HrmOffboardingTask();
        t.setId(nextIdFor(offboardingRepository.findAll().stream().map(HrmOffboardingTask::getId).toList(), "OFF", 3, 1));
        t.setEmployee(employee);
        t.setEmployeeName(employee == null ? nvl(payload.get("employeeName")) : employee.getName());
        t.setTask(nvl(payload.get("task")));
        if (t.getTask().isBlank()) invalidRequest("Tên nhiệm vụ không được để trống.");
        t.setOwner(nvl(payload.get("owner")));
        t.setDueDate(str(payload.get("dueDate")));
        t.setStatus("Pending");
        t.setCreated(LocalDate.now().toString());
        offboardingRepository.save(t);
        audit("CREATE", "OffboardingTask", t.getId());
        return HrmJson.toMap(t);
    }

    private Map<String, Object> pagedTasks(List<Map<String, Object>> all, String employeeId, String status,
                                           Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = all.stream().filter(r -> {
            boolean me = (employeeId == null || employeeId.isBlank()) || Objects.equals(str(r.get("employeeId")), employeeId);
            boolean ms = (status == null || status.isBlank()) || Objects.equals(str(r.get("status")), status);
            return me && ms;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "id", "desc");
        Map<String, Object> r = new LinkedHashMap<>(paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize));
        r.put("allItems", sorted);
        return r;
    }

    // ---------------------------------------------------------------- leave policies
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLeavePolicies() {
        return HrmJson.toMaps(leavePolicyRepository.findAll());
    }

    @Transactional
    public Map<String, Object> createLeavePolicy(Map<String, Object> payload) {
        HrmLeavePolicy p = HrmJson.fromMap(payload, HrmLeavePolicy.class);
        p.setId(nextIdFor(leavePolicyRepository.findAll().stream().map(HrmLeavePolicy::getId).toList(), "LP", 3, 1));
        if (p.getName() == null || p.getName().isBlank()) invalidRequest("Tên chính sách không được để trống.");
        fillAttributes(payload, p);
        leavePolicyRepository.save(p);
        audit("CREATE", "LeavePolicy", p.getId());
        return HrmJson.toMap(p);
    }

    @Transactional
    public Map<String, Object> updateLeavePolicy(String id, Map<String, Object> payload) {
        HrmLeavePolicy p = leavePolicyRepository.findById(id).orElse(null);
        if (p == null) notFound("Chính sách nghỉ phép");
        HrmLeavePolicy u = HrmJson.fromMap(payload, HrmLeavePolicy.class);
        if (u.getName() != null) p.setName(u.getName());
        if (u.getType() != null) p.setType(u.getType());
        if (u.getDays() != null) p.setDays(u.getDays());
        if (u.getCarryOver() != null) p.setCarryOver(u.getCarryOver());
        if (u.getDescription() != null) p.setDescription(u.getDescription());
        if (u.getStatus() != null) p.setStatus(u.getStatus());
        p.getAttributes().putAll(payload);
        leavePolicyRepository.save(p);
        audit("UPDATE", "LeavePolicy", id);
        return HrmJson.toMap(p);
    }

    /** Cộng dồn số ngày nghỉ hàng tháng cho nhân viên đang làm việc. Idempotent theo tháng. */
    @Transactional
    public Map<String, Object> runLeaveAccrual() {
        String period = YearMonth.now().toString();
        String marker = "SYSTEM#ACCRUAL#" + period;
        Map<String, Object> r = new LinkedHashMap<>();
        if (referenceDataRepository.existsById(marker)) {
            r.put("processed", 0);
            r.put("date", LocalDate.now().toString());
            r.put("status", "Already Run");
            return r;
        }
        int processed = 0;
        for (HrmEmployee e : employeeRepository.findAll()) {
            if (!"Active".equals(e.getStatus())) continue;
            HrmLeaveBalance b = leaveBalanceRepository.findByEmployee_Id(e.getId()).stream().findFirst()
                    .orElseGet(() -> newLeaveBalance(e));
            b.setAnnualTotal(b.getAnnualTotal() + 1);
            leaveBalanceRepository.save(b);
            processed++;
        }
        referenceDataRepository.save(new HrmReferenceData(marker, "systemMarkers", period, "Accrual " + period, 0));
        r.put("processed", processed);
        r.put("date", LocalDate.now().toString());
        r.put("status", "Completed");
        audit("RUN", "LeaveAccrual", period);
        return r;
    }

    // ---------------------------------------------------------------- shifts & rosters
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getShiftSchedules() {
        return HrmJson.toMaps(shiftScheduleRepository.findAll());
    }

    @Transactional
    public Map<String, Object> createShiftSchedule(Map<String, Object> payload) {
        HrmShiftSchedule s = HrmJson.fromMap(payload, HrmShiftSchedule.class);
        s.setId(nextIdFor(shiftScheduleRepository.findAll().stream().map(HrmShiftSchedule::getId).toList(), "SH", 3, 1));
        if (s.getName() == null || s.getName().isBlank()) invalidRequest("Tên ca làm không được để trống.");
        fillAttributes(payload, s);
        shiftScheduleRepository.save(s);
        audit("CREATE", "ShiftSchedule", s.getId());
        return HrmJson.toMap(s);
    }

    @Transactional
    public Map<String, Object> updateShiftSchedule(String id, Map<String, Object> payload) {
        HrmShiftSchedule s = shiftScheduleRepository.findById(id).orElse(null);
        if (s == null) notFound("Lịch ca");
        HrmShiftSchedule u = HrmJson.fromMap(payload, HrmShiftSchedule.class);
        if (u.getName() != null) s.setName(u.getName());
        if (u.getStartTime() != null) s.setStartTime(u.getStartTime());
        if (u.getEndTime() != null) s.setEndTime(u.getEndTime());
        if (u.getBreakMinutes() != null) s.setBreakMinutes(u.getBreakMinutes());
        if (u.getLocation() != null) s.setLocation(u.getLocation());
        s.getAttributes().putAll(payload);
        shiftScheduleRepository.save(s);
        audit("UPDATE", "ShiftSchedule", id);
        return HrmJson.toMap(s);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRosters(String month, String department) {
        List<Map<String, Object>> filtered = HrmJson.toMaps(rosterRepository.findAll()).stream().filter(r -> {
            boolean mm = (month == null || month.isBlank()) || Objects.equals(str(r.get("month")), month);
            boolean md = (department == null || department.isBlank()) || Objects.equals(str(r.get("department")), department);
            return mm && md;
        }).toList();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("items", filtered);
        r.put("month", month);
        r.put("department", department);
        return r;
    }

    @Transactional
    public Map<String, Object> assignRoster(Map<String, Object> payload) {
        String month = str(payload.get("month"));
        if (month == null || !month.matches("\\d{4}-\\d{2}")) invalidRequest("Tháng phân ca phải có dạng YYYY-MM.");
        String employeeId = normalizeId(payload.get("employeeId"));
        String date = str(payload.get("date"));
        if (date != null) requireIsoDate(date, "date");
        if (!month.equals(date == null ? "" : date.substring(0, 7))) invalidRequest("Ngày phân ca phải thuộc tháng đã chọn.");
        HrmRoster item = HrmJson.fromMap(payload, HrmRoster.class);
        item.setId(nextIdFor(rosterRepository.findAll().stream().map(HrmRoster::getId).toList(), "RS", 3, 1));
        item.setEmployeeId(employeeId);
        HrmEmployee employee = findEmployeeEntity(employeeId);
        if (employee == null) notFound("Nhân viên");
        if (!month.equals(date == null ? "" : date.substring(0, 7))) invalidRequest("Ngày phân ca không hợp lệ.");
        item.setEmployeeName(employee.getName());
        item.setDepartment(employee.getDepartment());
        fillAttributes(payload, item);
        rosterRepository.save(item);
        audit("CREATE", "Roster", item.getId());
        return HrmJson.toMap(item);
    }

    // ---------------------------------------------------------------- benefits
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBenefits() {
        return HrmJson.toMaps(benefitRepository.findAll());
    }

    @Transactional
    public Map<String, Object> createBenefit(Map<String, Object> payload) {
        HrmBenefit b = HrmJson.fromMap(payload, HrmBenefit.class);
        b.setId(nextIdFor(benefitRepository.findAll().stream().map(HrmBenefit::getId).toList(), "BN", 3, 1));
        if (b.getName() == null || b.getName().isBlank()) invalidRequest("Tên phúc lợi không được để trống.");
        fillAttributes(payload, b);
        benefitRepository.save(b);
        audit("CREATE", "Benefit", b.getId());
        return HrmJson.toMap(b);
    }

    @Transactional
    public Map<String, Object> updateBenefit(String id, Map<String, Object> payload) {
        HrmBenefit b = benefitRepository.findById(id).orElse(null);
        if (b == null) notFound("Phúc lợi");
        HrmBenefit u = HrmJson.fromMap(payload, HrmBenefit.class);
        if (u.getName() != null) b.setName(u.getName());
        if (u.getType() != null) b.setType(u.getType());
        if (u.getDescription() != null) b.setDescription(u.getDescription());
        if (u.getStatus() != null) b.setStatus(u.getStatus());
        b.getAttributes().putAll(payload);
        benefitRepository.save(b);
        audit("UPDATE", "Benefit", id);
        return HrmJson.toMap(b);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getBenefitEnrollments(String employeeId) {
        List<HrmBenefitEnrollment> items = (employeeId == null || employeeId.isBlank())
                ? benefitEnrollmentRepository.findAll() : benefitEnrollmentRepository.findAllByEmployeeId(employeeId);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("items", HrmJson.toMaps(items));
        return r;
    }

    @Transactional
    public Map<String, Object> createBenefitEnrollment(Map<String, Object> payload) {
        String employeeId = normalizeId(payload.get("employeeId"));
        HrmEmployee employee = findEmployeeEntity(employeeId);
        if (employee == null) notFound("Nhân viên");
        HrmBenefit benefit = benefitRepository.findById(str(payload.get("benefitId"))).orElse(null);
        if (benefit == null) notFound("Phúc lợi");
        if (benefitEnrollmentRepository.findAll().stream().anyMatch(e -> employeeId.equals(e.getEmployeeId())
                && benefit.getId().equals(e.getBenefitId()))) invalidRequest("Nhân viên đã đăng ký phúc lợi này.");
        HrmBenefitEnrollment item = HrmJson.fromMap(payload, HrmBenefitEnrollment.class);
        item.setId(nextIdFor(benefitEnrollmentRepository.findAll().stream().map(HrmBenefitEnrollment::getId).toList(), "BE", 3, 1));
        item.setEmployeeId(employeeId);
        item.setEmployeeName(employee.getName());
        item.setBenefitId(benefit.getId());
        item.setBenefitName(benefit.getName());
        item.setEnrolledDate(LocalDate.now().toString());
        item.setStatus("Active");
        fillAttributes(payload, item);
        benefitEnrollmentRepository.save(item);
        audit("CREATE", "BenefitEnrollment", item.getId());
        return HrmJson.toMap(item);
    }

    // ---------------------------------------------------------------- HR requests
    @Transactional(readOnly = true)
    public Map<String, Object> getHrRequests(String status, String employeeId, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = HrmJson.toMaps(hrRequestRepository.findAll()).stream().filter(r -> {
            boolean ms = (status == null || status.isBlank()) || Objects.equals(str(r.get("status")), status);
            boolean me = (employeeId == null || employeeId.isBlank()) || Objects.equals(str(r.get("employeeId")), employeeId);
            return ms && me;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "id", "desc");
        Map<String, Object> r = new LinkedHashMap<>(paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize));
        r.put("allItems", sorted);
        return r;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getHrRequestById(String id) {
        HrmHrRequest item = hrRequestRepository.findById(id).orElse(null);
        if (item == null) notFound("Yêu cầu nhân sự");
        return HrmJson.toMap(item);
    }

    @Transactional
    public Map<String, Object> createHrRequest(Map<String, Object> payload) {
        String employeeId = normalizeId(payload.get("employeeId"));
        HrmEmployee employee = findEmployeeEntity(employeeId);
        HrmHrRequest item = HrmJson.fromMap(payload, HrmHrRequest.class);
        item.setId(nextIdFor(hrRequestRepository.findAll().stream().map(HrmHrRequest::getId).toList(), "HR", 3, 1));
        item.setEmployeeId(employeeId);
        item.setEmployeeName(employee == null ? nvl(payload.get("employeeName")) : employee.getName());
        item.setStatus("Open");
        item.setCreated(LocalDate.now().toString());
        fillAttributes(payload, item);
        hrRequestRepository.save(item);
        audit("CREATE", "HrRequest", item.getId());
        return HrmJson.toMap(item);
    }

    @Transactional
    public Map<String, Object> updateHrRequestStatus(String id, String status) {
        requireIn(status, "hrRequestStatuses", "Trạng thái yêu cầu");
        HrmHrRequest item = hrRequestRepository.findById(id).orElse(null);
        if (item == null) notFound("Yêu cầu nhân sự");
        item.setStatus(status);
        hrRequestRepository.save(item);
        audit("UPDATE_STATUS", "HrRequest", id);
        return HrmJson.toMap(item);
    }

    // ---------------------------------------------------------------- announcements / events / birthdays
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAnnouncements() {
        return HrmJson.toMaps(announcementRepository.findAll());
    }

    @Transactional
    public Map<String, Object> createAnnouncement(Map<String, Object> payload) {
        HrmAnnouncement a = HrmJson.fromMap(payload, HrmAnnouncement.class);
        a.setId(nextIdFor(announcementRepository.findAll().stream().map(HrmAnnouncement::getId).toList(), "ANN", 3, 1));
        if (a.getTitle() == null || a.getTitle().isBlank()) invalidRequest("Tiêu đề thông báo không được để trống.");
        a.setCreated(LocalDate.now().toString());
        fillAttributes(payload, a);
        announcementRepository.save(a);
        audit("CREATE", "Announcement", a.getId());
        return HrmJson.toMap(a);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCompanyEvents(String month) {
        List<HrmCompanyEvent> all = companyEventRepository.findAll();
        if (month == null || month.isBlank()) return HrmJson.toMaps(all);
        return HrmJson.toMaps(all.stream().filter(e -> str(e.getDate()).startsWith(month)).toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBirthdays(String month) {
        String m = (month == null || month.isBlank()) ? YearMonth.now().toString() : month;
        String mm = m.length() >= 7 ? m.substring(5, 7) : m;
        List<Map<String, Object>> r = new ArrayList<>();
        for (Map<String, Object> e : HrmJson.toMaps(employeeRepository.findAll())) {
            String dob = str(e.get("dateOfBirth"));
            if (dob != null && dob.length() >= 7 && dob.substring(5, 7).equals(mm)) {
                Map<String, Object> b = new LinkedHashMap<>();
                b.put("employeeId", e.get("id"));
                b.put("name", e.get("name"));
                b.put("dateOfBirth", dob);
                r.add(b);
            }
        }
        return r;
    }

    // ---------------------------------------------------------------- import / export / audit
    @Transactional(readOnly = true)
    public List<Map<String, Object>> exportEmployees() {
        return HrmJson.toMaps(employeeRepository.findAll());
    }

    @Transactional
    public Map<String, Object> importEmployees(List<Map<String, Object>> payload) {
        int count = 0;
        if (payload != null) {
            for (Map<String, Object> p : payload) {
                createEmployee(p);
                count++;
            }
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("imported", count);
        return r;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAuditLogs() {
        return auditService.findAll();
    }
}
