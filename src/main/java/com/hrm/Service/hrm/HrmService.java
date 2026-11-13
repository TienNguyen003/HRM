package com.hrm.Service.hrm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class HrmService {

    final ObjectMapper objectMapper;

    List<Map<String, Object>> employees = new ArrayList<>();
    List<Map<String, Object>> attendanceRecords = new ArrayList<>();
    List<Map<String, Object>> documents = new ArrayList<>();
    List<Map<String, Object>> leaveRequests = new ArrayList<>();
    List<Map<String, Object>> leaveBalances = new ArrayList<>();
    List<Map<String, Object>> activityLog = new ArrayList<>();
    List<Map<String, Object>> candidates = new ArrayList<>();
    List<Map<String, Object>> interviews = new ArrayList<>();
    List<Map<String, Object>> goals = new ArrayList<>();
    List<Map<String, Object>> kpis = new ArrayList<>();
    List<Map<String, Object>> reviews = new ArrayList<>();
    List<Map<String, Object>> feedback = new ArrayList<>();
    List<Map<String, Object>> notifications = new ArrayList<>();
    List<Map<String, Object>> positions = new ArrayList<>();
    List<Map<String, Object>> growth = new ArrayList<>();
    List<Map<String, Object>> overtimeRequests = new ArrayList<>();
    List<Map<String, Object>> payrollRuns = new ArrayList<>();
    List<Map<String, Object>> payslips = new ArrayList<>();
    List<Map<String, Object>> onboardingTasks = new ArrayList<>();
    List<Map<String, Object>> offboardingTasks = new ArrayList<>();
    List<Map<String, Object>> leavePolicies = new ArrayList<>();
    List<Map<String, Object>> shiftSchedules = new ArrayList<>();
    List<Map<String, Object>> rosters = new ArrayList<>();
    List<Map<String, Object>> benefits = new ArrayList<>();
    List<Map<String, Object>> benefitEnrollments = new ArrayList<>();
    List<Map<String, Object>> hrRequests = new ArrayList<>();
    List<Map<String, Object>> announcements = new ArrayList<>();
    List<Map<String, Object>> companyEvents = new ArrayList<>();
    List<Map<String, Object>> auditLogs = new ArrayList<>();

    Map<String, Object> meta = new LinkedHashMap<>();

    @PostConstruct
    public void loadSeed() {
        try (InputStream is = new ClassPathResource("hrm-seed.json").getInputStream()) {
            Map<String, Object> seed = objectMapper.readValue(is, new TypeReference<>() {});
            meta = (Map<String, Object>) seed.get("meta");
            employees = new ArrayList<>((List<Map<String, Object>>) seed.get("employees"));
            attendanceRecords = new ArrayList<>((List<Map<String, Object>>) seed.get("attendanceRecords"));
            documents = new ArrayList<>((List<Map<String, Object>>) seed.get("documents"));
            leaveRequests = new ArrayList<>((List<Map<String, Object>>) seed.get("leaveRequests"));
            leaveBalances = new ArrayList<>((List<Map<String, Object>>) seed.get("leaveBalances"));
            activityLog = new ArrayList<>((List<Map<String, Object>>) seed.get("activityLog"));
            candidates = new ArrayList<>((List<Map<String, Object>>) seed.get("candidates"));
            interviews = new ArrayList<>((List<Map<String, Object>>) seed.get("interviews"));
            goals = new ArrayList<>((List<Map<String, Object>>) seed.get("goals"));
            kpis = new ArrayList<>((List<Map<String, Object>>) seed.get("kpis"));
            reviews = new ArrayList<>((List<Map<String, Object>>) seed.get("reviews"));
            feedback = new ArrayList<>((List<Map<String, Object>>) seed.get("feedback"));
            notifications = new ArrayList<>((List<Map<String, Object>>) seed.get("notifications"));
            positions = new ArrayList<>((List<Map<String, Object>>) seed.get("positions"));
            growth = new ArrayList<>((List<Map<String, Object>>) seed.get("growth"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // helpers
    private boolean matches(Object value, String query) {
        return String.valueOf(value == null ? "" : value).toLowerCase().contains(String.valueOf(query == null ? "" : query).trim().toLowerCase());
    }

    private List<String> uniqueStr(List<Map<String, Object>> collection, String key) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (Map<String, Object> item : collection) set.add(String.valueOf(item.get(key)));
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
            if (a instanceof Number && b instanceof Number) cmp = Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
            else cmp = String.valueOf(a == null ? "" : a).compareToIgnoreCase(String.valueOf(b == null ? "" : b));
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

    private String nextId(List<Map<String, Object>> collection, String prefix, int width, int start) {
        int highest = start - 1;
        for (Map<String, Object> item : collection) {
            String id = String.valueOf(item.get("id"));
            String numeric = id.replaceAll("\\D", "");
            try { int n = Integer.parseInt(numeric); if (n > highest) highest = n; } catch (Exception ignored) {}
        }
        return prefix + "-" + String.format("%0" + width + "d", highest + 1);
    }

    private String initialsOf(String name) {
        String[] parts = String.valueOf(name).trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        return sb.length() == 0 ? "NA" : sb.toString();
    }

    private String formatDisplayDate(String iso) {
        if (iso == null || iso.isBlank()) return "";
        try {
            java.time.LocalDate d = java.time.LocalDate.parse(iso);
            return d.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) { return iso; }
    }

    private Map<String, Object> findEmployee(String id) {
        for (Map<String, Object> e : employees) if (String.valueOf(e.get("id")).equalsIgnoreCase(String.valueOf(id))) return e;
        return null;
    }

    private int findEmployeeIndex(String id) {
        for (int i = 0; i < employees.size(); i++) if (String.valueOf(employees.get(i).get("id")).equalsIgnoreCase(String.valueOf(id))) return i;
        return -1;
    }

    private void pushActivity(String employeeId, String type, String detail, String icon, String tone) {
        Map<String, Object> act = new LinkedHashMap<>();
        act.put("id", "ACT-" + (activityLog.size() + 1));
        act.put("employeeId", employeeId);
        act.put("type", type);
        act.put("detail", detail);
        act.put("date", "Just now");
        act.put("icon", icon);
        act.put("tone", tone);
        activityLog.add(0, act);
    }

    public Map<String, Object> getFilterOptions() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("departments", meta.get("departments"));
        r.put("locations", meta.get("locations"));
        r.put("employmentTypes", meta.get("employmentTypes"));
        r.put("employeeStatuses", meta.get("employeeStatuses"));
        r.put("attendanceStatuses", meta.get("attendanceStatuses"));
        r.put("leaveTypes", meta.get("leaveTypes"));
        r.put("leaveStatuses", meta.get("leaveStatuses"));
        r.put("recruitmentStages", meta.get("recruitmentStages"));
        r.put("goalPriorities", meta.get("goalPriorities"));
        return r;
    }
    public Map<String, Object> getEmployeeDirectory(String search, String department, String status, String position, String employmentType, String location, String sort, String sortDirection, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = employees.stream().filter(e -> {
            boolean ms = (search == null || search.isBlank()) || matches(e.get("name"), search) || matches(e.get("id"), search) || matches(e.get("email"), search) || matches(e.get("position"), search) || matches(e.get("phone"), search);
            boolean md = (department == null || department.isBlank()) || String.valueOf(e.get("department")).equals(department);
            boolean mst = (status == null || status.isBlank()) || String.valueOf(e.get("status")).equals(status);
            boolean mp = (position == null || position.isBlank()) || String.valueOf(e.get("position")).equals(position);
            boolean me = (employmentType == null || employmentType.isBlank()) || String.valueOf(e.get("employmentType")).equals(employmentType);
            boolean ml = (location == null || location.isBlank()) || String.valueOf(e.get("location")).equals(location);
            return ms && md && mst && mp && me && ml;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, sort == null || sort.isBlank() ? "name" : sort, sortDirection == null ? "asc" : sortDirection);
        Map<String, Object> pageObj = paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize);
        Map<String, Object> r = new LinkedHashMap<>(pageObj);
        r.put("allItems", new ArrayList<>(sorted));
        r.put("departments", uniqueStr(employees, "department"));
        r.put("positions", uniqueStr(employees, "position"));
        r.put("locations", uniqueStr(employees, "location"));
        r.put("employmentTypes", uniqueStr(employees, "employmentType"));
        return r;
    }

    public Map<String, Object> getEmployeeSummary() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", employees.size());
        r.put("active", employees.stream().filter(e -> "Active".equals(e.get("status"))).count());
        r.put("onLeave", employees.stream().filter(e -> "On Leave".equals(e.get("status"))).count());
        r.put("inactive", employees.stream().filter(e -> "Inactive".equals(e.get("status"))).count());
        r.put("departments", uniqueStr(employees, "department").size());
        return r;
    }

    public Map<String, Object> getEmployeeById(String id) {
        Map<String, Object> emp = findEmployee(id);
        if (emp == null) return null;
        Map<String, Object> r = new LinkedHashMap<>(emp);
        List<Map<String, Object>> att = attendanceRecords.stream().filter(x -> String.valueOf(x.get("employeeId")).equalsIgnoreCase(id)).collect(Collectors.toList());
        List<Map<String, Object>> lv = leaveRequests.stream().filter(x -> String.valueOf(x.get("employeeId")).equalsIgnoreCase(id)).collect(Collectors.toList());
        Map<String, Object> bal = leaveBalances.stream().filter(x -> String.valueOf(x.get("employeeId")).equalsIgnoreCase(id)).findFirst().orElse(null);
        List<Map<String, Object>> docs = documents.stream().filter(x -> String.valueOf(x.get("employeeId")).equalsIgnoreCase(id)).collect(Collectors.toList());
        List<Map<String, Object>> act = activityLog.stream().filter(x -> String.valueOf(x.get("employeeId")).equalsIgnoreCase(id)).collect(Collectors.toList());
        List<String> dr = employees.stream().filter(x -> String.valueOf(x.get("manager")).equals(emp.get("name"))).map(x -> String.valueOf(x.get("id"))).collect(Collectors.toList());
        r.put("attendance", att);
        r.put("leave", lv);
        r.put("balance", bal);
        r.put("documents", docs);
        r.put("activity", act);
        r.put("directReports", dr);
        return r;
    }
    public Map<String, Object> createEmployee(Map<String, Object> payload) {
        String requestedId = String.valueOf(payload.getOrDefault("employeeId", "")).trim().toUpperCase();
        boolean useRequested = requestedId.matches("EMP-\\d{5}") && employees.stream().noneMatch(e -> String.valueOf(e.get("id")).toUpperCase().equals(requestedId));
        String id = useRequested ? requestedId : nextId(employees, "EMP", 5, 132);
        String firstName = String.valueOf(payload.getOrDefault("firstName", ""));
        String lastName = String.valueOf(payload.getOrDefault("lastName", ""));
        String name = (firstName + " " + lastName).trim();
        if (name.isBlank()) name = "New Employee";
        Map<String, Object> emergencyContact = (Map<String, Object>) payload.get("emergencyContact");
        if (emergencyContact == null) {
            Map<String, Object> ec = new LinkedHashMap<>();
            ec.put("name", payload.getOrDefault("emergencyName", ""));
            ec.put("relation", payload.getOrDefault("emergencyRelation", "Family"));
            ec.put("phone", payload.getOrDefault("emergencyPhone", ""));
            emergencyContact = ec;
        }
        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("id", id);
        employee.put("initials", initialsOf(name));
        employee.put("name", name);
        employee.put("position", payload.getOrDefault("position", "Team Member"));
        employee.put("department", payload.getOrDefault("department", "Unassigned"));
        employee.put("manager", payload.getOrDefault("manager", "Not assigned"));
        employee.put("employmentType", payload.getOrDefault("employmentType", "Full-time"));
        employee.put("status", payload.getOrDefault("status", "Active"));
        String joiningIso = String.valueOf(payload.getOrDefault("joiningDate", java.time.LocalDate.now().toString()));
        employee.put("joinedDate", formatDisplayDate(joiningIso));
        employee.put("joinedDateIso", joiningIso);
        employee.put("email", payload.getOrDefault("email", ""));
        employee.put("phone", payload.getOrDefault("phone", ""));
        employee.put("dateOfBirth", payload.getOrDefault("dateOfBirth", ""));
        employee.put("address", payload.getOrDefault("address", ""));
        employee.put("emergencyContact", emergencyContact);
        employee.put("location", payload.getOrDefault("location", "Ho Chi Minh City"));
        employee.put("attendanceRate", 0);
        employee.put("leaveBalance", 20);
        employee.put("goals", 0);
        employees.add(employee);

        String todayIso = java.time.LocalDate.now().toString();
        List<Map<String, Object>> newAtt = new ArrayList<>();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("id", "ATT-" + id + "-" + todayIso);
        r.put("employeeId", id);
        r.put("employeeName", name);
        r.put("department", employee.get("department"));
        r.put("date", todayIso);
        r.put("dateLabel", formatDisplayDate(todayIso));
        r.put("weekday", java.time.LocalDate.now().getDayOfWeek().toString().substring(0,3));
        r.put("checkIn", "08:30");
        r.put("checkOut", "--");
        r.put("hours", "In progress");
        r.put("lateMinutes", 0);
        r.put("status", "Present");
        newAtt.add(r);
        attendanceRecords.addAll(0, newAtt);

        Map<String, Object> bal = new LinkedHashMap<>();
        Map<String, Object> annual = new LinkedHashMap<>(); annual.put("total",20); annual.put("used",0); annual.put("pending",0);
        Map<String, Object> sick = new LinkedHashMap<>(); sick.put("total",12); sick.put("used",0); sick.put("pending",0);
        Map<String, Object> unpaid = new LinkedHashMap<>(); unpaid.put("total",0); unpaid.put("used",0); unpaid.put("pending",0);
        bal.put("id", id); bal.put("employeeId", id); bal.put("employeeName", name); bal.put("department", employee.get("department"));
        bal.put("annual", annual); bal.put("sick", sick); bal.put("unpaid", unpaid);
        leaveBalances.add(bal);

        pushActivity(id, "Employee created", name + " was added to " + employee.get("department") + ".", "fas fa-user-plus", "success");
        return employee;
    }

    public Map<String, Object> updateEmployee(String id, Map<String, Object> payload) {
        int idx = findEmployeeIndex(id);
        if (idx < 0) return null;
        Map<String, Object> emp = employees.get(idx);
        for (Map.Entry<String, Object> e : payload.entrySet()) {
            String k = e.getKey();
            if (k.equals("employeeId") || k.equals("firstName") || k.equals("lastName")) continue;
            emp.put(k, e.getValue());
        }
        if (payload.containsKey("name")) emp.put("name", payload.get("name"));
        if (payload.containsKey("position")) emp.put("position", payload.get("position"));
        employees.set(idx, emp);
        pushActivity(id, "Profile updated", String.valueOf(emp.get("name")) + "'s profile was updated.", "fas fa-user-pen", "info");
        return emp;
    }

    public Map<String, Object> deleteEmployee(String id) {
        Map<String, Object> emp = findEmployee(id);
        if (emp == null) return null;
        employees.removeIf(e -> String.valueOf(e.get("id")).equalsIgnoreCase(id));
        attendanceRecords.removeIf(r -> String.valueOf(r.get("employeeId")).equalsIgnoreCase(id));
        documents.removeIf(d -> String.valueOf(d.get("employeeId")).equalsIgnoreCase(id));
        leaveRequests.removeIf(l -> String.valueOf(l.get("employeeId")).equalsIgnoreCase(id));
        leaveBalances.removeIf(b -> String.valueOf(b.get("employeeId")).equalsIgnoreCase(id));
        return emp;
    }

    public boolean isEmployeeIdAvailable(String id) {
        if (id == null || id.isBlank()) return true;
        return employees.stream().noneMatch(e -> String.valueOf(e.get("id")).equalsIgnoreCase(id));
    }
    public Map<String, Object> getOrganizationOverview() {
        List<String> depts = uniqueStr(employees, "department");
        List<Map<String, Object>> byDep = new ArrayList<>();
        for (String name : depts) {
            List<Map<String, Object>> members = employees.stream().filter(e -> String.valueOf(e.get("department")).equals(name)).collect(Collectors.toList());
            Map<String, Object> lead = members.stream().filter(m -> employees.stream().anyMatch(x -> String.valueOf(x.get("manager")).equals(m.get("name")))).findFirst().orElse(members.isEmpty() ? null : members.get(0));
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
                mm.put("id", m.get("id")); mm.put("name", m.get("name")); mm.put("position", m.get("position")); mm.put("status", m.get("status")); mm.put("initials", m.get("initials"));
                mems.add(mm);
            }
            d.put("members", mems);
            byDep.add(d);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("departments", byDep);
        r.put("total", employees.size());
        r.put("locations", uniqueStr(employees, "location"));
        return r;
    }
    public Map<String, Object> getDepartmentDetail(String name) {
        String department = name == null ? "" : name.trim();
        if (department.isBlank()) return null;
        List<Map<String, Object>> members = employees.stream().filter(e -> String.valueOf(e.get("department")).equals(department)).collect(Collectors.toList());
        if (members.isEmpty()) return null;
        Map<String, Object> lead = members.stream().filter(m -> employees.stream().anyMatch(x -> String.valueOf(x.get("manager")).equals(m.get("name")))).findFirst().orElse(members.get(0));
        java.util.function.BiFunction<List<Map<String, Object>>, String, Map<String, Integer>> countBy = (list, key) -> {
            Map<String, Integer> acc = new LinkedHashMap<>();
            for (Map<String, Object> item : list) {
                String k = String.valueOf(item.get(key));
                acc.put(k, acc.getOrDefault(k, 0) + 1);
            }
            return acc;
        };
        Map<String, Integer> posCount = countBy.apply(members, "position");
        Map<String, Integer> empTypeCount = countBy.apply(members, "employmentType");
        Map<String, Integer> statusCount = countBy.apply(members, "status");
        List<Map<String, Object>> positions = new ArrayList<>();
        for (Map.Entry<String, Integer> e : posCount.entrySet()) { Map<String,Object> m=new LinkedHashMap<>(); m.put("label",e.getKey()); m.put("value",e.getValue()); positions.add(m); }
        positions.sort((a,b)->Integer.compare((int)b.get("value"),(int)a.get("value")));
        List<Map<String, Object>> empTypes = new ArrayList<>();
        for (Map.Entry<String, Integer> e : empTypeCount.entrySet()) { Map<String,Object> m=new LinkedHashMap<>(); m.put("label",e.getKey()); m.put("value",e.getValue()); empTypes.add(m); }
        List<Map<String, Object>> statuses = new ArrayList<>();
        for (Map.Entry<String, Integer> e : statusCount.entrySet()) { Map<String,Object> m=new LinkedHashMap<>(); m.put("label",e.getKey()); m.put("value",e.getValue()); statuses.add(m); }

        Set<String> memberIds = members.stream().map(m -> String.valueOf(m.get("id"))).collect(Collectors.toSet());
        String today = java.time.LocalDate.now().toString();
        List<Map<String, Object>> todayRecords = attendanceRecords.stream().filter(r -> memberIds.contains(String.valueOf(r.get("employeeId"))) && today.equals(r.get("date"))).collect(Collectors.toList());
        List<Map<String, Object>> deptLeave = leaveRequests.stream().filter(r -> memberIds.contains(String.valueOf(r.get("employeeId")))).collect(Collectors.toList());

        Map<String, Object> attendanceToday = new LinkedHashMap<>();
        attendanceToday.put("present", todayRecords.stream().filter(r -> "Present".equals(r.get("status"))).count());
        attendanceToday.put("late", todayRecords.stream().filter(r -> "Late".equals(r.get("status"))).count());
        attendanceToday.put("remote", todayRecords.stream().filter(r -> "Working From Home".equals(r.get("status"))).count());
        attendanceToday.put("onLeave", todayRecords.stream().filter(r -> "On Leave".equals(r.get("status"))).count());
        attendanceToday.put("absent", todayRecords.stream().filter(r -> "Absent".equals(r.get("status"))).count());

        double avgTenure = members.stream().mapToDouble(m -> {
            try {
                String iso = String.valueOf(m.get("joinedDateIso") != null ? m.get("joinedDateIso") : m.get("joinedDate"));
                java.time.LocalDate joined = java.time.LocalDate.parse(iso);
                return (System.currentTimeMillis() - joined.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()) / (1000.0*60*60*24*365);
            } catch (Exception ex) { return 0; }
        }).average().orElse(0);

        Map<String, Object> manager = null;
        if (lead != null) {
            manager = new LinkedHashMap<>();
            manager.put("id", lead.get("id")); manager.put("name", lead.get("name")); manager.put("position", lead.get("position"));
            manager.put("email", lead.get("email")); manager.put("phone", lead.get("phone")); manager.put("initials", lead.get("initials"));
            manager.put("status", lead.get("status")); manager.put("joiningDate", lead.get("joinedDate"));
        }

        List<Map<String, Object>> mems = new ArrayList<>();
        for (Map<String, Object> m : members) {
            Map<String, Object> mm = new LinkedHashMap<>();
            mm.put("id", m.get("id")); mm.put("name", m.get("name")); mm.put("initials", m.get("initials"));
            mm.put("position", m.get("position")); mm.put("status", m.get("status")); mm.put("employmentType", m.get("employmentType"));
            mm.put("email", m.get("email")); mm.put("phone", m.get("phone")); mm.put("location", m.get("location"));
            mm.put("manager", m.get("manager"));
            String iso = String.valueOf(m.get("joinedDateIso") != null ? m.get("joinedDateIso") : m.get("joinedDate"));
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
        r.put("leaveApprovedDays", deptLeave.stream().filter(x -> "Approved".equals(x.get("status"))).mapToInt(x -> ((Number) x.getOrDefault("days", 0)).intValue()).sum());
        r.put("members", mems);
        return r;
    }
    public Map<String, Object> getOrganizationChart() {
        Map<String, Object> rootEmp = employees.stream().filter(e -> "Board of Directors".equals(e.get("manager"))).findFirst().orElse(employees.isEmpty() ? null : employees.get(0));
        Map<String, Object> root = buildOrgNode(rootEmp, new HashSet<>());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("root", root);
        r.put("total", employees.size());
        return r;
    }

    private Map<String, Object> buildOrgNode(Map<String, Object> employee, Set<String> visited) {
        if (employee == null || visited.contains(String.valueOf(employee.get("id")))) return null;
        Set<String> next = new HashSet<>(visited);
        next.add(String.valueOf(employee.get("id")));
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("id", employee.get("id"));
        node.put("name", employee.get("name"));
        node.put("position", employee.get("position"));
        node.put("department", employee.get("department"));
        node.put("manager", employee.get("manager"));
        node.put("initials", employee.get("initials"));
        List<Map<String, Object>> reports = new ArrayList<>();
        for (Map<String, Object> rep : employees) {
            if (String.valueOf(rep.get("manager")).equals(employee.get("name"))) {
                Map<String, Object> child = buildOrgNode(rep, next);
                if (child != null) reports.add(child);
            }
        }
        node.put("reports", reports);
        return node;
    }
    public Map<String, Object> getAttendanceRecords(String date, String department, String employeeId, String status, String sort, String sortDirection, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = attendanceRecords.stream().filter(r -> {
            boolean md = (date == null || date.isBlank()) || String.valueOf(r.get("date")).equals(date);
            boolean mdep = (department == null || department.isBlank()) || String.valueOf(r.get("department")).equals(department);
            boolean me = (employeeId == null || employeeId.isBlank()) || String.valueOf(r.get("employeeId")).equals(employeeId);
            boolean ms = (status == null || status.isBlank()) || String.valueOf(r.get("status")).equals(status);
            return md && mdep && me && ms;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, sort, sortDirection);
        Map<String, Object> pageObj = paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize);
        Map<String, Object> r = new LinkedHashMap<>(pageObj);
        r.put("allItems", new ArrayList<>(sorted));
        return r;
    }

    public Map<String, Object> getAttendanceSummary() {
        String today = java.time.LocalDate.now().toString();
        List<Map<String, Object>> todayRecords = attendanceRecords.stream().filter(r -> today.equals(r.get("date"))).collect(Collectors.toList());
        List<Map<String, Object>> weekdayRecords = attendanceRecords.stream().filter(r -> !Arrays.asList("Sat","Sun").contains(r.get("weekday"))).collect(Collectors.toList());
        Map<String, Integer> countBy = new LinkedHashMap<>();
        for (Map<String, Object> r : weekdayRecords) { String st=String.valueOf(r.get("status")); countBy.put(st, countBy.getOrDefault(st,0)+1); }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("date", today);
        r.put("present", todayRecords.stream().filter(x -> "Present".equals(x.get("status"))).count());
        r.put("late", todayRecords.stream().filter(x -> "Late".equals(x.get("status"))).count());
        r.put("absent", todayRecords.stream().filter(x -> "Absent".equals(x.get("status"))).count());
        r.put("onLeave", todayRecords.stream().filter(x -> "On Leave".equals(x.get("status"))).count());
        r.put("remote", todayRecords.stream().filter(x -> "Working From Home".equals(x.get("status"))).count());
        r.put("totalEmployees", employees.size());
        r.put("statusBreakdown", countBy);
        r.put("lateEmployees", attendanceRecords.stream().filter(x -> "Late".equals(x.get("status"))).limit(6).collect(Collectors.toList()));
        List<Map<String, Object>> deptBreakdown = new ArrayList<>();
        for (String name : uniqueStr(employees, "department")) {
            List<Map<String, Object>> rows = attendanceRecords.stream().filter(x -> name.equals(x.get("department"))).collect(Collectors.toList());
            long present = rows.stream().filter(x -> "Present".equals(x.get("status")) || "Working From Home".equals(x.get("status"))).count();
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("name", name); d.put("total", rows.size()); d.put("present", (int)present);
            d.put("rate", rows.size() == 0 ? 0 : Math.round((double)present / rows.size() * 100));
            deptBreakdown.add(d);
        }
        r.put("departmentBreakdown", deptBreakdown);
        return r;
    }
    public Map<String, Object> getAttendanceCalendar(String month, String department) {
        String m = (month == null || month.isBlank()) ? "2026-09" : month;
        List<Map<String, Object>> rows = attendanceRecords.stream().filter(r -> (department == null || department.isBlank() || department.equals(r.get("department"))) && String.valueOf(r.get("date")).startsWith(m)).collect(Collectors.toList());
        Map<String, Map<String, Integer>> byDate = new LinkedHashMap<>();
        for (Map<String, Object> rec : rows) {
            String d = String.valueOf(rec.get("date"));
            byDate.putIfAbsent(d, new LinkedHashMap<>(Map.of("present",0,"late",0,"absent",0,"onLeave",0,"remote",0)));
            Map<String, Integer> acc = byDate.get(d);
            String st = String.valueOf(rec.get("status"));
            if ("Present".equals(st)) acc.put("present", acc.get("present")+1);
            if ("Late".equals(st)) acc.put("late", acc.get("late")+1);
            if ("Absent".equals(st)) acc.put("absent", acc.get("absent")+1);
            if ("On Leave".equals(st)) acc.put("onLeave", acc.get("onLeave")+1);
            if ("Working From Home".equals(st)) acc.put("remote", acc.get("remote")+1);
        }
        String[] parts = m.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthIndex = Integer.parseInt(parts[1]);
        int daysInMonth = java.time.YearMonth.of(year, monthIndex).lengthOfMonth();
        List<Map<String, Object>> cells = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            String date = String.format("%s-%02d", m, i);
            Map<String, Object> cell = new LinkedHashMap<>();
            cell.put("date", date);
            cell.put("day", i);
            Map<String, Integer> acc = byDate.getOrDefault(date, Map.of("present",0,"late",0,"absent",0,"onLeave",0,"remote",0));
            cell.putAll(acc);
            cells.add(cell);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("month", m);
        r.put("cells", cells);
        r.put("weekdays", Arrays.asList("Mon","Tue","Wed","Thu","Fri","Sat","Sun"));
        return r;
    }

    public Map<String, Object> checkIn(String employeeId) {
        Map<String, Object> employee = findEmployee(employeeId);
        if (employee == null) return null;
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now();
        String stamp = String.format("%02d:%02d", now.getHour(), now.getMinute());
        String iso = now.toLocalDate().toString();
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("id", "ATT-" + employeeId + "-" + iso);
        record.put("employeeId", employeeId);
        record.put("employeeName", employee.get("name"));
        record.put("department", employee.get("department"));
        record.put("date", iso);
        record.put("dateLabel", formatDisplayDate(iso));
        record.put("weekday", now.getDayOfWeek().toString().substring(0,3));
        record.put("checkIn", stamp);
        record.put("checkOut", "--");
        record.put("hours", "In progress");
        record.put("lateMinutes", 0);
        record.put("status", "Present");
        attendanceRecords.removeIf(x -> String.valueOf(x.get("id")).equals(record.get("id")));
        attendanceRecords.add(0, record);
        return record;
    }

    public Map<String, Object> checkOut(String employeeId) {
        Map<String, Object> record = attendanceRecords.stream().filter(x -> String.valueOf(x.get("employeeId")).equals(employeeId) && "--".equals(x.get("checkOut"))).findFirst().orElse(null);
        if (record == null) return null;
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now();
        record.put("checkOut", String.format("%02d:%02d", now.getHour(), now.getMinute()));
        record.put("hours", "Recorded");
        return record;
    }
    public Map<String, Object> getLeaveRequests(String search, String status, String type, String employeeId, String sort, String sortDirection, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = leaveRequests.stream().filter(item -> {
            boolean ms = (status == null || status.isBlank()) || String.valueOf(item.get("status")).equals(status);
            boolean me = (employeeId == null || employeeId.isBlank()) || String.valueOf(item.get("employeeId")).equals(employeeId);
            boolean mt = (type == null || type.isBlank()) || String.valueOf(item.get("type")).equals(type);
            boolean mq = (search == null || search.isBlank()) || matches(item.get("employeeName"), search) || matches(item.get("id"), search) || matches(item.get("reason"), search);
            return ms && me && mt && mq;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, sort, sortDirection);
        Map<String, Object> pageObj = paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize);
        Map<String, Object> r = new LinkedHashMap<>(pageObj);
        r.put("allItems", new ArrayList<>(sorted));
        return r;
    }

    public Map<String, Object> getLeaveSummary() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("pending", leaveRequests.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        r.put("approved", leaveRequests.stream().filter(x -> "Approved".equals(x.get("status"))).count());
        r.put("rejected", leaveRequests.stream().filter(x -> "Rejected".equals(x.get("status"))).count());
        r.put("cancelled", leaveRequests.stream().filter(x -> "Cancelled".equals(x.get("status"))).count());
        String todayIso = java.time.LocalDate.now().toString();
        r.put("onLeaveToday", leaveRequests.stream().filter(x -> "Approved".equals(x.get("status")) && String.valueOf(x.get("start")).compareTo(todayIso) <= 0 && String.valueOf(x.get("end")).compareTo(todayIso) >= 0).count());
        List<Map<String, Object>> byType = new ArrayList<>();
        for (String t : (List<String>) meta.get("leaveTypes")) {
            long v = leaveRequests.stream().filter(x -> t.equals(x.get("type"))).count();
            if (v > 0) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",t); m.put("value",(int)v); byType.add(m); }
        }
        r.put("byType", byType);
        return r;
    }

    public List<Map<String, Object>> getLeaveBalances(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) return new ArrayList<>(leaveBalances);
        return leaveBalances.stream().filter(b -> String.valueOf(b.get("employeeId")).equals(employeeId)).collect(Collectors.toList());
    }

    public Map<String, Object> getLeaveById(String id) {
        Map<String, Object> item = leaveRequests.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (item == null) return null;
        Map<String, Object> r = new LinkedHashMap<>(item);
        List<Map<String, Object>> timeline = new ArrayList<>();
        Map<String,Object> t1=new LinkedHashMap<>(); t1.put("label","Employee submitted request"); t1.put("date", item.get("submitted")); t1.put("state","done");
        Map<String,Object> t2=new LinkedHashMap<>(); t2.put("label","Manager review"); t2.put("date", "Pending".equals(item.get("status")) ? "Awaiting decision" : item.get("approver")); t2.put("state", "Pending".equals(item.get("status")) ? "current" : "done");
        Map<String,Object> t3=new LinkedHashMap<>(); t3.put("label", "Pending".equals(item.get("status")) ? "Final decision" : item.get("status")); t3.put("date", "Pending".equals(item.get("status")) ? "--" : item.get("approver")); t3.put("state", "Pending".equals(item.get("status")) ? "upcoming" : "done");
        timeline.add(t1); timeline.add(t2); timeline.add(t3);
        r.put("timeline", timeline);
        return r;
    }
    public Map<String, Object> createLeaveRequest(Map<String, Object> payload) {
        String employeeId = String.valueOf(payload.get("employeeId"));
        Map<String, Object> employee = findEmployee(employeeId);
        if (employee == null) return null;
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(leaveRequests, "LV", 3, 1));
        item.put("employeeId", employee.get("id"));
        item.put("employeeName", employee.get("name"));
        item.put("type", payload.getOrDefault("type", "Annual Leave"));
        item.put("start", payload.get("start"));
        item.put("end", payload.get("end"));
        item.put("days", ((Number) payload.getOrDefault("days", 1)).intValue());
        item.put("status", "Pending");
        item.put("approver", employee.get("manager"));
        item.put("submitted", java.time.LocalDate.now().toString());
        item.put("reason", payload.getOrDefault("reason", ""));
        leaveRequests.add(0, item);
        Map<String, Object> balance = leaveBalances.stream().filter(b -> String.valueOf(b.get("employeeId")).equals(employeeId)).findFirst().orElse(null);
        if (balance != null) {
            String bucket = "Sick Leave".equals(item.get("type")) ? "sick" : "Unpaid Leave".equals(item.get("type")) ? "unpaid" : "annual";
            Map<String, Object> b = (Map<String, Object>) balance.get(bucket);
            b.put("pending", ((Number) b.getOrDefault("pending", 0)).intValue() + ((Number) item.get("days")).intValue());
        }
        pushActivity(employeeId, "Leave requested", item.get("days") + " day(s) of " + item.get("type") + " requested.", "fas fa-calendar-plus", "info");
        return item;
    }

    public Map<String, Object> updateLeaveStatus(String id, String status, String approver) {
        List<String> statuses = (List<String>) meta.get("leaveStatuses");
        if (statuses == null || !statuses.contains(status)) return null;
        Map<String, Object> item = leaveRequests.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (item == null) return null;
        item.put("status", status);
        item.put("approver", approver == null || approver.isBlank() ? item.get("approver") : approver);
        String employeeId = String.valueOf(item.get("employeeId"));
        Map<String, Object> balance = leaveBalances.stream().filter(b -> String.valueOf(b.get("employeeId")).equals(employeeId)).findFirst().orElse(null);
        if (balance != null) {
            String bucket = "Sick Leave".equals(item.get("type")) ? "sick" : "Unpaid Leave".equals(item.get("type")) ? "unpaid" : "annual";
            Map<String, Object> b = (Map<String, Object>) balance.get(bucket);
            int days = ((Number) item.get("days")).intValue();
            b.put("pending", Math.max(0, ((Number) b.getOrDefault("pending", 0)).intValue() - days));
            if ("Approved".equals(status)) b.put("used", ((Number) b.getOrDefault("used", 0)).intValue() + days);
        }
        pushActivity(employeeId, "Leave " + status.toLowerCase(), item.get("days") + " day(s) of " + item.get("type") + " was " + status.toLowerCase() + ".", "fas fa-calendar-check", "Approved".equals(status) ? "success" : "danger");
        return item;
    }
    public Map<String, Object> getRecruitmentOverview() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("candidates", new ArrayList<>(candidates));
        r.put("positions", new ArrayList<>(positions));
        return r;
    }
    public Map<String, Object> createCandidate(Map<String, Object> payload) {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("id", nextId(candidates, "CND", 3, 1));
        c.putAll(payload);
        if (!c.containsKey("stage")) c.put("stage", "Applied");
        candidates.add(0, c);
        return c;
    }
    public Map<String, Object> updateCandidate(String id, Map<String, Object> payload) {
        Map<String, Object> c = candidates.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (c == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) c.put(e.getKey(), e.getValue());
        return c;
    }
    public Map<String, Object> deleteCandidate(String id) {
        Map<String, Object> c = candidates.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (c == null) return null;
        candidates.removeIf(x -> String.valueOf(x.get("id")).equals(id));
        return c;
    }
    public Map<String, Object> updateInterview(String id, Map<String, Object> payload) {
        Map<String, Object> iv = interviews.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (iv == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) iv.put(e.getKey(), e.getValue());
        return iv;
    }
    public Map<String, Object> cancelInterview(String id) {
        Map<String, Object> iv = interviews.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (iv == null) return null;
        iv.put("status", "Cancelled");
        return iv;
    }
    public Map<String, Object> updatePosition(String id, Map<String, Object> payload) {
        Map<String, Object> p = positions.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (p == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) p.put(e.getKey(), e.getValue());
        return p;
    }
    public Map<String, Object> closePosition(String id) {
        Map<String, Object> p = positions.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (p == null) return null;
        p.put("status", "Closed");
        return p;
    }

    public Map<String, Object> getCandidateById(String id) {
        Map<String, Object> candidate = candidates.stream().filter(c -> String.valueOf(c.get("id")).equals(id)).findFirst().orElse(null);
        if (candidate == null) return null;
        Map<String, Object> r = new LinkedHashMap<>(candidate);
        List<Map<String, Object>> ivs = interviews.stream().filter(i -> String.valueOf(i.get("candidateId")).equals(id)).collect(Collectors.toList());
        r.put("interviews", ivs);
        return r;
    }

    public Map<String, Object> updateCandidateStage(String id, String stage) {
        List<String> stages = (List<String>) meta.get("recruitmentStages");
        if (stages == null || !stages.contains(stage)) return null;
        Map<String, Object> candidate = candidates.stream().filter(c -> String.valueOf(c.get("id")).equals(id)).findFirst().orElse(null);
        if (candidate == null) return null;
        candidate.put("stage", stage);
        List<Map<String, Object>> history = (List<Map<String, Object>>) candidate.getOrDefault("stageHistory", new ArrayList<>());
        Map<String, Object> h = new LinkedHashMap<>(); h.put("stage", stage); h.put("date", java.time.LocalDate.now().toString());
        history.add(h);
        candidate.put("stageHistory", history);
        return candidate;
    }

    public Map<String, Object> scheduleInterview(Map<String, Object> payload) {
        String candidateId = String.valueOf(payload.get("candidateId"));
        Map<String, Object> candidate = candidates.stream().filter(c -> String.valueOf(c.get("id")).equals(candidateId)).findFirst().orElse(null);
        if (candidate == null) return null;
        Map<String, Object> interview = new LinkedHashMap<>();
        interview.put("id", nextId(interviews, "ITV", 2, 1));
        interview.put("candidateId", candidate.get("id"));
        interview.put("candidateName", candidate.get("name"));
        interview.put("role", candidate.get("role"));
        interview.put("date", payload.get("date"));
        interview.put("time", payload.getOrDefault("time", "10:00"));
        interview.put("duration", ((Number) payload.getOrDefault("duration", 60)).intValue());
        interview.put("mode", payload.getOrDefault("mode", "Google Meet"));
        interview.put("interviewer", payload.getOrDefault("interviewer", "Unassigned"));
        interview.put("status", "Scheduled");
        interviews.add(0, interview);
        return interview;
    }

    public Map<String, Object> createPosition(Map<String, Object> payload) {
        Map<String, Object> position = new LinkedHashMap<>();
        position.put("id", nextId(positions, "POS", 2, 1));
        position.put("title", payload.get("title"));
        position.put("department", payload.get("department"));
        position.put("location", payload.getOrDefault("location", "Ho Chi Minh City"));
        position.put("type", payload.getOrDefault("type", "Full-time"));
        position.put("status", "Open");
        position.put("candidates", 0);
        position.put("created", "25 Sep 2026");
        position.put("openings", ((Number) payload.getOrDefault("openings", 1)).intValue());
        position.put("description", payload.getOrDefault("description", ""));
        positions.add(0, position);
        return position;
    }

    public Map<String, Object> getRecruitmentSummary() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("openPositions", positions.stream().filter(p -> "Open".equals(p.get("status"))).count());
        r.put("totalCandidates", candidates.size());
        r.put("interviewing", candidates.stream().filter(c -> "Interview".equals(c.get("stage"))).count());
        r.put("offers", candidates.stream().filter(c -> "Offer".equals(c.get("stage"))).count());
        r.put("hired", candidates.stream().filter(c -> "Hired".equals(c.get("stage"))).count());
        List<Map<String, Object>> pipeline = new ArrayList<>();
        for (String s : (List<String>) meta.get("recruitmentStages")) {
            Map<String,Object> m=new LinkedHashMap<>(); m.put("name",s); m.put("value", candidates.stream().filter(c -> s.equals(c.get("stage"))).count()); pipeline.add(m);
        }
        r.put("pipeline", pipeline);
        r.put("interviews", interviews.stream().limit(5).collect(Collectors.toList()));
        return r;
    }
    public Map<String, Object> getPerformanceOverview() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("goals", new ArrayList<>(goals));
        r.put("reviews", new ArrayList<>(reviews));
        r.put("feedback", new ArrayList<>(feedback));
        r.put("kpis", new ArrayList<>(kpis));
        r.put("employees", employees.stream().filter(e -> "Active".equals(e.get("status"))).collect(Collectors.toList()));
        return r;
    }

    public Map<String, Object> createGoal(Map<String, Object> payload) {
        Map<String, Object> goal = new LinkedHashMap<>();
        goal.put("id", nextId(goals, "GOAL", 2, 1));
        goal.put("title", payload.get("title"));
        goal.put("owner", payload.get("owner"));
        goal.put("department", payload.get("department"));
        goal.put("progress", 0);
        String due = payload.get("due") == null ? "" : String.valueOf(payload.get("due"));
        goal.put("due", due.isBlank() ? "31 Dec 2026" : formatDisplayDate(due));
        goal.put("dueIso", due.isBlank() ? "2026-12-31" : due);
        goal.put("status", "On Track");
        goal.put("priority", payload.getOrDefault("priority", "Medium"));
        goal.put("metric", payload.getOrDefault("metric", "Progress"));
        goal.put("target", payload.getOrDefault("target", "100%"));
        goals.add(0, goal);
        return goal;
    }

    public Map<String, Object> updateGoalProgress(String id, Object progress) {
        Map<String, Object> goal = goals.stream().filter(g -> String.valueOf(g.get("id")).equals(id)).findFirst().orElse(null);
        if (goal == null) return null;
        int p = 0;
        try { p = (int) Math.round(Double.parseDouble(String.valueOf(progress))); } catch (Exception e) { p = 0; }
        p = Math.min(100, Math.max(0, p));
        goal.put("progress", p);
        goal.put("status", p < 40 ? "At Risk" : "On Track");
        return goal;
    }

    public Map<String, Object> getReviewById(String id) {
        return reviews.stream().filter(r -> String.valueOf(r.get("id")).equals(id)).findFirst().orElse(null);
    }
    public Map<String, Object> updateReview(String id, Map<String, Object> payload) {
        Map<String, Object> r = reviews.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (r == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) r.put(e.getKey(), e.getValue());
        return r;
    }
    public Map<String, Object> completeReview(String id, Map<String, Object> payload) {
        Map<String, Object> r = reviews.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (r == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) r.put(e.getKey(), e.getValue());
        r.put("status", "Completed");
        r.put("completedDate", java.time.LocalDate.now().toString());
        return r;
    }
    // KPI
    public List<Map<String, Object>> getKpis() { return new ArrayList<>(kpis); }
    public Map<String, Object> createKpi(Map<String, Object> payload) {
        Map<String, Object> k = new LinkedHashMap<>();
        k.put("id", nextId(kpis, "KPI", 3, 1));
        k.putAll(payload);
        if (!k.containsKey("status")) k.put("status", "Active");
        kpis.add(0, k);
        return k;
    }
    public Map<String, Object> updateKpi(String id, Map<String, Object> payload) {
        Map<String, Object> k = kpis.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (k == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) k.put(e.getKey(), e.getValue());
        return k;
    }
    public Map<String, Object> deleteKpi(String id) {
        Map<String, Object> k = kpis.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (k == null) return null;
        kpis.removeIf(x -> String.valueOf(x.get("id")).equals(id));
        return k;
    }
    // Feedback
    public Map<String, Object> createFeedback(Map<String, Object> payload) {
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("id", nextId(feedback, "FB", 3, 1));
        f.putAll(payload);
        f.put("created", java.time.LocalDate.now().toString());
        feedback.add(0, f);
        return f;
    }
    public Map<String, Object> updateFeedback(String id, Map<String, Object> payload) {
        Map<String, Object> f = feedback.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (f == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) f.put(e.getKey(), e.getValue());
        return f;
    }
    // Goal assignment already in createGoal via owner/department
    public Map<String, Object> addEmployeeDocument(String employeeId, Map<String, Object> payload) {
        if (findEmployee(employeeId) == null) return null;
        Map<String, Object> document = new LinkedHashMap<>();
        document.put("id", nextId(documents, "DOC", 2, 1));
        document.put("employeeId", employeeId);
        document.put("name", payload.getOrDefault("name", "Document.pdf"));
        document.put("type", payload.getOrDefault("type", "Other"));
        document.put("size", payload.getOrDefault("size", "1.0 MB"));
        document.put("updated", "Just now");
        document.put("expires", payload.getOrDefault("expires", null));
        documents.add(document);
        pushActivity(employeeId, "Document uploaded", document.get("name") + " was added to the profile.", "fas fa-file-arrow-up", "primary");
        return document;
    }

    public Map<String, Object> removeEmployeeDocument(String documentId) {
        Map<String, Object> document = documents.stream().filter(d -> String.valueOf(d.get("id")).equals(documentId)).findFirst().orElse(null);
        if (document == null) return null;
        documents.removeIf(d -> String.valueOf(d.get("id")).equals(documentId));
        pushActivity(String.valueOf(document.get("employeeId")), "Document removed", document.get("name") + " was removed from the profile.", "fas fa-file-circle-xmark", "warning");
        return document;
    }
    public List<Map<String, Object>> getNotifications() { return new ArrayList<>(notifications); }
    public long getUnreadNotificationCount() { return notifications.stream().filter(n -> Boolean.TRUE.equals(n.get("unread"))).count(); }
    public Map<String, Object> markNotificationRead(String id) {
        Map<String, Object> n = notifications.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (n == null) return null;
        n.put("unread", false);
        return n;
    }
    public List<Map<String, Object>> markAllNotificationsRead() {
        for (Map<String, Object> n : notifications) n.put("unread", false);
        return new ArrayList<>(notifications);
    }

    public Map<String, Object> getDashboardOverview() {
        String today = java.time.LocalDate.now().toString();
        List<Map<String, Object>> todayRecords = attendanceRecords.stream().filter(r -> today.equals(r.get("date"))).collect(Collectors.toList());
        Map<String, Object> r = new LinkedHashMap<>();
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalEmployees", employees.size());
        metrics.put("activeEmployees", employees.stream().filter(e -> "Active".equals(e.get("status"))).count());
        String monthStart = java.time.LocalDate.now().withDayOfMonth(1).toString();
        metrics.put("newEmployees", employees.stream().filter(e -> String.valueOf(e.getOrDefault("joinedDateIso","")).compareTo(monthStart) >= 0).count());
        metrics.put("onLeave", employees.stream().filter(e -> "On Leave".equals(e.get("status"))).count());
        metrics.put("absentToday", todayRecords.stream().filter(x -> "Absent".equals(x.get("status"))).count());
        metrics.put("openPositions", positions.stream().filter(p -> "Open".equals(p.get("status"))).count());
        metrics.put("pendingRequests", leaveRequests.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        metrics.put("pendingReviews", reviews.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        r.put("metrics", metrics);
        r.put("growth", new ArrayList<>(growth));
        List<Map<String, Object>> deptDist = new ArrayList<>();
        for (String name : uniqueStr(employees, "department")) {
            Map<String,Object> m=new LinkedHashMap<>(); m.put("name",name); m.put("value", employees.stream().filter(e -> name.equals(e.get("department"))).count()); deptDist.add(m);
        }
        r.put("departmentDistribution", deptDist);
        List<Map<String, Object>> attBreak = new ArrayList<>();
        for (String s : (List<String>) meta.get("attendanceStatuses")) {
            Map<String,Object> m=new LinkedHashMap<>(); m.put("name",s); m.put("value", attendanceRecords.stream().filter(x -> s.equals(x.get("status"))).count()); attBreak.add(m);
        }
        r.put("attendanceBreakdown", attBreak);
        List<Map<String, Object>> leaveStats = new ArrayList<>();
        for (String s : (List<String>) meta.get("leaveStatuses")) {
            Map<String,Object> m=new LinkedHashMap<>(); m.put("name",s); m.put("value", leaveRequests.stream().filter(x -> s.equals(x.get("status"))).count()); leaveStats.add(m);
        }
        r.put("leaveStats", leaveStats);
        List<Map<String, Object>> hiring = new ArrayList<>();
        for (String s : (List<String>) meta.get("recruitmentStages")) {
            Map<String,Object> m=new LinkedHashMap<>(); m.put("name",s); m.put("value", candidates.stream().filter(c -> s.equals(c.get("stage"))).count()); hiring.add(m);
        }
        r.put("hiringPipeline", hiring);
        r.put("recentActivity", activityLog.stream().limit(6).collect(Collectors.toList()));
        r.put("upcomingLeave", leaveRequests.stream().filter(x -> "Pending".equals(x.get("status")) || "Approved".equals(x.get("status"))).limit(5).collect(Collectors.toList()));
        return r;
    }
    public Map<String, Object> getReportsOverview() {
        List<String> locations = uniqueStr(employees, "location");
        List<Map<String, Object>> locationDistribution = new ArrayList<>();
        for (String name : locations) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",name); m.put("value", employees.stream().filter(e -> name.equals(e.get("location"))).count()); locationDistribution.add(m); }
        List<String> empTypes = uniqueStr(employees, "employmentType");
        List<Map<String, Object>> employmentTypeDistribution = new ArrayList<>();
        for (String name : empTypes) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",name); m.put("value", employees.stream().filter(e -> name.equals(e.get("employmentType"))).count()); employmentTypeDistribution.add(m); }
        List<String> statuses = uniqueStr(employees, "status");
        List<Map<String, Object>> statusDistribution = new ArrayList<>();
        for (String name : statuses) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",name); m.put("value", employees.stream().filter(e -> name.equals(e.get("status"))).count()); statusDistribution.add(m); }
        List<String> goalStatuses = uniqueStr(goals, "status");
        List<Map<String, Object>> goalStatusDistribution = new ArrayList<>();
        for (String name : goalStatuses) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",name); m.put("value", goals.stream().filter(g -> name.equals(g.get("status"))).count()); goalStatusDistribution.add(m); }
        List<String> reviewStatuses = uniqueStr(reviews, "status");
        List<Map<String, Object>> reviewStatusDistribution = new ArrayList<>();
        for (String name : reviewStatuses) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",name); m.put("value", reviews.stream().filter(r -> name.equals(r.get("status"))).count()); reviewStatusDistribution.add(m); }
        List<String> kpiStatuses = uniqueStr(kpis, "status");
        List<Map<String, Object>> kpiStatusDistribution = new ArrayList<>();
        for (String name : kpiStatuses) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",name); m.put("value", kpis.stream().filter(k -> name.equals(k.get("status"))).count()); kpiStatusDistribution.add(m); }
        List<Map<String, Object>> lateByDepartment = new ArrayList<>();
        for (String name : uniqueStr(employees, "department")) {
            Map<String,Object> m=new LinkedHashMap<>(); m.put("name",name);
            m.put("value", attendanceRecords.stream().filter(r -> {
                Map<String,Object> emp = findEmployee(String.valueOf(r.get("employeeId")));
                return emp != null && name.equals(emp.get("department")) && "Late".equals(r.get("status"));
            }).count());
            lateByDepartment.add(m);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalEmployees", employees.size());
        metrics.put("activeEmployees", employees.stream().filter(e -> "Active".equals(e.get("status"))).count());
        metrics.put("onLeave", employees.stream().filter(e -> "On Leave".equals(e.get("status"))).count());
        metrics.put("inactive", employees.stream().filter(e -> "Inactive".equals(e.get("status"))).count());
        metrics.put("openPositions", positions.stream().filter(p -> "Open".equals(p.get("status"))).count());
        metrics.put("totalCandidates", candidates.size());
        metrics.put("hiredCandidates", candidates.stream().filter(c -> "Hired".equals(c.get("stage"))).count());
        metrics.put("pendingLeave", leaveRequests.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        metrics.put("approvedLeaveDays", leaveRequests.stream().filter(x -> "Approved".equals(x.get("status"))).mapToInt(x -> ((Number) x.getOrDefault("days",0)).intValue()).sum());
        metrics.put("pendingReviews", reviews.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        metrics.put("activeGoals", goals.stream().filter(g -> !"Completed".equals(g.get("status"))).count());
        metrics.put("completedGoals", goals.stream().filter(g -> "Completed".equals(g.get("status"))).count());
        r.put("metrics", metrics);
        r.put("growth", new ArrayList<>(growth));
        List<Map<String, Object>> deptDist = new ArrayList<>();
        for (String name : uniqueStr(employees, "department")) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",name); m.put("value", employees.stream().filter(e -> name.equals(e.get("department"))).count()); deptDist.add(m); }
        r.put("departmentDistribution", deptDist);
        r.put("locationDistribution", locationDistribution);
        r.put("employmentTypeDistribution", employmentTypeDistribution);
        r.put("statusDistribution", statusDistribution);
        List<Map<String, Object>> attBreak = new ArrayList<>();
        for (String s : (List<String>) meta.get("attendanceStatuses")) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",s); m.put("value", attendanceRecords.stream().filter(x -> s.equals(x.get("status"))).count()); attBreak.add(m); }
        r.put("attendanceBreakdown", attBreak);
        List<Map<String, Object>> leaveStats = new ArrayList<>();
        for (String s : (List<String>) meta.get("leaveStatuses")) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",s); m.put("value", leaveRequests.stream().filter(x -> s.equals(x.get("status"))).count()); leaveStats.add(m); }
        r.put("leaveStats", leaveStats);
        List<Map<String, Object>> hiring = new ArrayList<>();
        for (String s : (List<String>) meta.get("recruitmentStages")) { Map<String,Object> m=new LinkedHashMap<>(); m.put("name",s); m.put("value", candidates.stream().filter(c -> s.equals(c.get("stage"))).count()); hiring.add(m); }
        r.put("hiringPipeline", hiring);
        r.put("goalStatusDistribution", goalStatusDistribution);
        r.put("reviewStatusDistribution", reviewStatusDistribution);
        r.put("kpiStatusDistribution", kpiStatusDistribution);
        r.put("lateByDepartment", lateByDepartment);
        return r;
    }

    // ---------- Overtime ----------
    public Map<String, Object> getOvertime(String employeeId, String department, String status, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = overtimeRequests.stream().filter(r -> {
            boolean me = (employeeId == null || employeeId.isBlank()) || String.valueOf(r.get("employeeId")).equals(employeeId);
            boolean md = (department == null || department.isBlank()) || String.valueOf(r.get("department")).equals(department);
            boolean ms = (status == null || status.isBlank()) || String.valueOf(r.get("status")).equals(status);
            return me && md && ms;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "date", "desc");
        Map<String, Object> pageObj = paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize);
        Map<String, Object> r = new LinkedHashMap<>(pageObj);
        r.put("allItems", new ArrayList<>(sorted));
        return r;
    }
    public Map<String, Object> getOvertimeSummary() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", overtimeRequests.size());
        r.put("pending", overtimeRequests.stream().filter(x -> "Pending".equals(x.get("status"))).count());
        r.put("approved", overtimeRequests.stream().filter(x -> "Approved".equals(x.get("status"))).count());
        r.put("rejected", overtimeRequests.stream().filter(x -> "Rejected".equals(x.get("status"))).count());
        r.put("totalHours", overtimeRequests.stream().mapToDouble(x -> {
            try { return Double.parseDouble(String.valueOf(x.getOrDefault("hours", 0))); } catch (Exception e) { return 0; }
        }).sum());
        return r;
    }
    public Map<String, Object> createOvertime(Map<String, Object> payload) {
        String employeeId = String.valueOf(payload.get("employeeId"));
        Map<String, Object> employee = findEmployee(employeeId);
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(overtimeRequests, "OT", 3, 1));
        item.put("employeeId", employeeId);
        item.put("employeeName", employee == null ? payload.getOrDefault("employeeName", "") : employee.get("name"));
        item.put("department", employee == null ? payload.getOrDefault("department", "") : employee.get("department"));
        item.put("date", payload.getOrDefault("date", java.time.LocalDate.now().toString()));
        item.put("hours", payload.getOrDefault("hours", 1));
        item.put("reason", payload.getOrDefault("reason", ""));
        item.put("status", "Pending");
        item.put("approver", employee == null ? payload.getOrDefault("approver", "") : employee.get("manager"));
        item.put("submitted", java.time.LocalDate.now().toString());
        overtimeRequests.add(0, item);
        return item;
    }
    public Map<String, Object> updateOvertimeStatus(String id, String status) {
        Map<String, Object> item = overtimeRequests.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (item == null) return null;
        item.put("status", status);
        return item;
    }

    // ---------- Payroll runs & payslips ----------
    public Map<String, Object> getPayrollRuns(String month, String department, String status, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = payrollRuns.stream().filter(r -> {
            boolean mm = (month == null || month.isBlank()) || String.valueOf(r.get("month")).equals(month);
            boolean md = (department == null || department.isBlank()) || String.valueOf(r.get("department")).equals(department);
            boolean ms = (status == null || status.isBlank()) || String.valueOf(r.get("status")).equals(status);
            return mm && md && ms;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "month", "desc");
        Map<String, Object> pageObj = paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize);
        Map<String, Object> r = new LinkedHashMap<>(pageObj);
        r.put("allItems", new ArrayList<>(sorted));
        return r;
    }
    public Map<String, Object> createPayrollRun(Map<String, Object> payload) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(payrollRuns, "PR", 3, 1));
        item.put("month", payload.getOrDefault("month", ""));
        item.put("department", payload.getOrDefault("department", "All"));
        item.put("amount", payload.getOrDefault("amount", 0));
        item.put("status", "Draft");
        item.put("created", java.time.LocalDate.now().toString());
        payrollRuns.add(0, item);
        return item;
    }
    public Map<String, Object> getPayrollRunById(String id) {
        return payrollRuns.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
    }
    public Map<String, Object> updatePayrollRunStatus(String id, String status) {
        Map<String, Object> item = getPayrollRunById(id);
        if (item == null) return null;
        item.put("status", status);
        return item;
    }
    public Map<String, Object> getPayslips(String employeeId, String month, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = payslips.stream().filter(r -> {
            boolean me = (employeeId == null || employeeId.isBlank()) || String.valueOf(r.get("employeeId")).equals(employeeId);
            boolean mm = (month == null || month.isBlank()) || String.valueOf(r.get("month")).equals(month);
            return me && mm;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "month", "desc");
        Map<String, Object> pageObj = paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize);
        Map<String, Object> r = new LinkedHashMap<>(pageObj);
        r.put("allItems", new ArrayList<>(sorted));
        return r;
    }
    public Map<String, Object> getPayslipById(String id) {
        return payslips.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
    }

    // ---------- Onboarding / Offboarding ----------
    public Map<String, Object> getOnboarding(String employeeId, String status, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = onboardingTasks.stream().filter(r -> {
            boolean me = (employeeId == null || employeeId.isBlank()) || String.valueOf(r.get("employeeId")).equals(employeeId);
            boolean ms = (status == null || status.isBlank()) || String.valueOf(r.get("status")).equals(status);
            return me && ms;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "id", "desc");
        Map<String, Object> pageObj = paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize);
        Map<String, Object> r = new LinkedHashMap<>(pageObj);
        r.put("allItems", new ArrayList<>(sorted));
        return r;
    }
    public Map<String, Object> createOnboarding(Map<String, Object> payload) {
        String employeeId = String.valueOf(payload.get("employeeId"));
        Map<String, Object> employee = findEmployee(employeeId);
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(onboardingTasks, "ONB", 3, 1));
        item.put("employeeId", employeeId);
        item.put("employeeName", employee == null ? payload.getOrDefault("employeeName", "") : employee.get("name"));
        item.put("task", payload.getOrDefault("task", ""));
        item.put("owner", payload.getOrDefault("owner", ""));
        item.put("dueDate", payload.getOrDefault("dueDate", ""));
        item.put("status", "Pending");
        item.put("created", java.time.LocalDate.now().toString());
        onboardingTasks.add(0, item);
        return item;
    }
    public Map<String, Object> updateOnboardingStatus(String id, String status) {
        Map<String, Object> item = onboardingTasks.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (item == null) return null;
        item.put("status", status);
        return item;
    }
    public Map<String, Object> getOffboarding(String employeeId, String status, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = offboardingTasks.stream().filter(r -> {
            boolean me = (employeeId == null || employeeId.isBlank()) || String.valueOf(r.get("employeeId")).equals(employeeId);
            boolean ms = (status == null || status.isBlank()) || String.valueOf(r.get("status")).equals(status);
            return me && ms;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "id", "desc");
        Map<String, Object> pageObj = paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize);
        Map<String, Object> r = new LinkedHashMap<>(pageObj);
        r.put("allItems", new ArrayList<>(sorted));
        return r;
    }
    public Map<String, Object> createOffboarding(Map<String, Object> payload) {
        String employeeId = String.valueOf(payload.get("employeeId"));
        Map<String, Object> employee = findEmployee(employeeId);
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(offboardingTasks, "OFF", 3, 1));
        item.put("employeeId", employeeId);
        item.put("employeeName", employee == null ? payload.getOrDefault("employeeName", "") : employee.get("name"));
        item.put("task", payload.getOrDefault("task", ""));
        item.put("owner", payload.getOrDefault("owner", ""));
        item.put("dueDate", payload.getOrDefault("dueDate", ""));
        item.put("status", "Pending");
        item.put("created", java.time.LocalDate.now().toString());
        offboardingTasks.add(0, item);
        return item;
    }

    // ---------- Leave policies ----------
    public List<Map<String, Object>> getLeavePolicies() { return new ArrayList<>(leavePolicies); }
    public Map<String, Object> createLeavePolicy(Map<String, Object> payload) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(leavePolicies, "LP", 3, 1));
        item.putAll(payload);
        if (!item.containsKey("id")) item.put("id", nextId(leavePolicies, "LP", 3, 1));
        leavePolicies.add(0, item);
        return item;
    }
    public Map<String, Object> updateLeavePolicy(String id, Map<String, Object> payload) {
        Map<String, Object> item = leavePolicies.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (item == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) item.put(e.getKey(), e.getValue());
        return item;
    }
    public Map<String, Object> runLeaveAccrual() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("processed", employees.size());
        r.put("date", java.time.LocalDate.now().toString());
        r.put("status", "Completed");
        return r;
    }

    // ---------- Shift scheduling / rosters ----------
    public List<Map<String, Object>> getShiftSchedules() { return new ArrayList<>(shiftSchedules); }
    public Map<String, Object> createShiftSchedule(Map<String, Object> payload) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(shiftSchedules, "SH", 3, 1));
        item.putAll(payload);
        shiftSchedules.add(0, item);
        return item;
    }
    public Map<String, Object> updateShiftSchedule(String id, Map<String, Object> payload) {
        Map<String, Object> item = shiftSchedules.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (item == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) item.put(e.getKey(), e.getValue());
        return item;
    }
    public Map<String, Object> getRosters(String month, String department) {
        List<Map<String, Object>> filtered = rosters.stream().filter(r -> {
            boolean mm = (month == null || month.isBlank()) || String.valueOf(r.get("month")).equals(month);
            boolean md = (department == null || department.isBlank()) || String.valueOf(r.get("department")).equals(department);
            return mm && md;
        }).collect(Collectors.toList());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("items", filtered);
        r.put("month", month);
        r.put("department", department);
        return r;
    }
    public Map<String, Object> assignRoster(Map<String, Object> payload) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(rosters, "RS", 3, 1));
        item.putAll(payload);
        rosters.add(0, item);
        return item;
    }

    // ---------- Benefits ----------
    public List<Map<String, Object>> getBenefits() { return new ArrayList<>(benefits); }
    public Map<String, Object> createBenefit(Map<String, Object> payload) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(benefits, "BN", 3, 1));
        item.putAll(payload);
        benefits.add(0, item);
        return item;
    }
    public Map<String, Object> updateBenefit(String id, Map<String, Object> payload) {
        Map<String, Object> item = benefits.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
        if (item == null) return null;
        for (Map.Entry<String, Object> e : payload.entrySet()) item.put(e.getKey(), e.getValue());
        return item;
    }
    public Map<String, Object> getBenefitEnrollments(String employeeId) {
        List<Map<String, Object>> filtered = benefitEnrollments.stream().filter(r -> (employeeId == null || employeeId.isBlank()) || String.valueOf(r.get("employeeId")).equals(employeeId)).collect(Collectors.toList());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("items", filtered);
        return r;
    }
    public Map<String, Object> createBenefitEnrollment(Map<String, Object> payload) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(benefitEnrollments, "BE", 3, 1));
        item.putAll(payload);
        benefitEnrollments.add(0, item);
        return item;
    }

    // ---------- HR Requests ----------
    public Map<String, Object> getHrRequests(String status, String employeeId, Integer page, Integer pageSize) {
        List<Map<String, Object>> filtered = hrRequests.stream().filter(r -> {
            boolean ms = (status == null || status.isBlank()) || String.valueOf(r.get("status")).equals(status);
            boolean me = (employeeId == null || employeeId.isBlank()) || String.valueOf(r.get("employeeId")).equals(employeeId);
            return ms && me;
        }).collect(Collectors.toList());
        List<Map<String, Object>> sorted = sortBy(filtered, "id", "desc");
        Map<String, Object> pageObj = paginate(sorted, page == null ? 1 : page, pageSize == null ? 10 : pageSize);
        Map<String, Object> r = new LinkedHashMap<>(pageObj);
        r.put("allItems", new ArrayList<>(sorted));
        return r;
    }
    public Map<String, Object> getHrRequestById(String id) {
        return hrRequests.stream().filter(x -> String.valueOf(x.get("id")).equals(id)).findFirst().orElse(null);
    }
    public Map<String, Object> createHrRequest(Map<String, Object> payload) {
        String employeeId = String.valueOf(payload.get("employeeId"));
        Map<String, Object> employee = findEmployee(employeeId);
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(hrRequests, "HR", 3, 1));
        item.put("employeeId", employeeId);
        item.put("employeeName", employee == null ? payload.getOrDefault("employeeName", "") : employee.get("name"));
        item.putAll(payload);
        item.put("status", "Open");
        item.put("created", java.time.LocalDate.now().toString());
        hrRequests.add(0, item);
        return item;
    }
    public Map<String, Object> updateHrRequestStatus(String id, String status) {
        Map<String, Object> item = getHrRequestById(id);
        if (item == null) return null;
        item.put("status", status);
        return item;
    }

    // ---------- Announcements / Events / Birthdays ----------
    public List<Map<String, Object>> getAnnouncements() { return new ArrayList<>(announcements); }
    public Map<String, Object> createAnnouncement(Map<String, Object> payload) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId(announcements, "ANN", 3, 1));
        item.putAll(payload);
        item.put("created", java.time.LocalDate.now().toString());
        announcements.add(0, item);
        return item;
    }
    public List<Map<String, Object>> getCompanyEvents(String month) {
        if (month == null || month.isBlank()) return new ArrayList<>(companyEvents);
        return companyEvents.stream().filter(e -> String.valueOf(e.getOrDefault("date","")).startsWith(month)).collect(Collectors.toList());
    }
    public List<Map<String, Object>> getBirthdays(String month) {
        String m = (month == null || month.isBlank()) ? java.time.YearMonth.now().toString() : month;
        String mm = m.length() >= 7 ? m.substring(5,7) : m;
        List<Map<String, Object>> r = new ArrayList<>();
        for (Map<String, Object> e : employees) {
            String dob = String.valueOf(e.getOrDefault("dateOfBirth", ""));
            if (dob.length() >= 7 && dob.substring(5,7).equals(mm)) {
                Map<String, Object> b = new LinkedHashMap<>();
                b.put("employeeId", e.get("id"));
                b.put("name", e.get("name"));
                b.put("dateOfBirth", dob);
                r.add(b);
            }
        }
        return r;
    }

    // ---------- Import / Export ----------
    public List<Map<String, Object>> exportEmployees() { return new ArrayList<>(employees); }
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

    // ---------- Audit logs ----------
    public List<Map<String, Object>> getAuditLogs() { return new ArrayList<>(auditLogs); }
    public void pushAudit(String action, String entity, String entityId, String actor) {
        Map<String, Object> a = new LinkedHashMap<>();
        a.put("id", "AUD-" + (auditLogs.size() + 1));
        a.put("action", action);
        a.put("entity", entity);
        a.put("entityId", entityId);
        a.put("actor", actor);
        a.put("date", java.time.LocalDateTime.now().toString());
        auditLogs.add(0, a);
    }

    // ---------- Filter options (extend) ----------
    // (already exists)

    // ---------- Self update helpers already in updateEmployee ----------

}
