package com.hrm.Service.hrm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hrm.Entity.hrm.*;
import com.hrm.Entity.user.Employee;
import com.hrm.repository.hrm.*;
import com.hrm.repository.user.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Khởi tạo dữ liệu cho module HRM.
 *
 * Phân loại:
 * - System/Configuration/Reference Seed: danh mục động từ meta (luôn chạy, chỉ insert nếu chưa có).
 * - Business Sample Seed: dữ liệu mẫu, chỉ chạy khi bật flag và chưa từng import (theo seed key).
 *
 * Không ghi đè bản ghi người dùng đã chỉnh sửa; có thể chạy lại an toàn.
 */
@Slf4j
@Component
@Order(50)
@RequiredArgsConstructor
public class HrmSeeder implements ApplicationRunner {

    private final HrmSeedProperties properties;
    private final HrmSeedLogRepository seedLogRepository;
    private final HrmReferenceDataRepository referenceDataRepository;

    private final EmployeeRepository employeeRepository;
    private final HrmAttendanceRepository attendanceRepository;
    private final HrmLeaveRequestRepository leaveRequestRepository;
    private final HrmLeaveBalanceRepository leaveBalanceRepository;
    private final HrmDocumentRepository documentRepository;
    private final HrmActivityLogRepository activityLogRepository;
    private final HrmCandidateRepository candidateRepository;
    private final HrmInterviewRepository interviewRepository;
    private final HrmPositionRepository positionRepository;
    private final HrmGoalRepository goalRepository;
    private final HrmKpiRepository kpiRepository;
    private final HrmReviewRepository reviewRepository;
    private final HrmFeedbackRepository feedbackRepository;
    private final HrmNotificationRepository notificationRepository;
    private final HrmGrowthRepository growthRepository;

    private static final List<String> META_KEYS = List.of(
            "departments", "locations", "employmentTypes", "employeeStatuses",
            "attendanceStatuses", "leaveTypes", "leaveStatuses",
            "recruitmentStages", "goalPriorities");

    /** Configuration Seed: trạng thái workflow, không hardcode trong business logic. */
    private static final Map<String, List<String>> CONFIG_REFERENCE = Map.of(
            "overtimeStatuses", List.of("Pending", "Approved", "Rejected"),
            "payrollStatuses", List.of("Draft", "Approved", "Paid", "Cancelled"),
            "hrRequestStatuses", List.of("Open", "In Progress", "Resolved", "Rejected", "Closed"),
            "taskStatuses", List.of("Pending", "In Progress", "Completed", "Cancelled"));

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, Object> seed;
        try (InputStream is = new ClassPathResource("hrm-seed.json").getInputStream()) {
            seed = HrmJson.mapper().readValue(is, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Không đọc được hrm-seed.json", e);
        }

        seedReference(seed);
        seedSample(seed);
    }

    /** Reference/Configuration seed: idempotent, không update bản ghi đã tồn tại. */
    private void seedReference(Map<String, Object> seed) {
        if (!properties.isReferenceEnabled()) return;
        Object rawMeta = seed.get("meta");
        if (!(rawMeta instanceof Map<?, ?> meta)) return;

        int count = 0;
        for (String key : META_KEYS) {
            Object raw = meta.get(key);
            if (!(raw instanceof List<?> values)) continue;
            count += insertReference(key, values);
        }
        for (Map.Entry<String, List<String>> entry : CONFIG_REFERENCE.entrySet()) {
            count += insertReference(entry.getKey(), entry.getValue());
        }
        log.info("[HRM Seed] Reference data: {} bản ghi mới", count);
    }

    private int insertReference(String category, List<?> values) {
        int count = 0;
        int order = 0;
        for (Object value : values) {
            String code = String.valueOf(value);
            String id = "REF#" + category.toUpperCase() + "#" + code;
            if (!referenceDataRepository.existsById(id)) {
                referenceDataRepository.save(new HrmReferenceData(id, category, code, code, order));
                count++;
            }
            order++;
        }
        return count;
    }

    /** Business Sample seed: chạy đúng 1 lần theo sampleKey. */
    @SuppressWarnings("unchecked")
    private void seedSample(Map<String, Object> seed) {
        if (!properties.isSampleEnabled()) {
            log.info("[HRM Seed] Sample data bị tắt (app.hrm.seed.sample-enabled=false)");
            return;
        }
        String key = properties.getSampleKey();
        if (seedLogRepository.existsById(key)) {
            log.info("[HRM Seed] Sample '{}' đã import trước đó -> bỏ qua", key);
            return;
        }
        if (properties.isSampleOnlyWhenEmpty() && !employeeRepository.findAll().isEmpty()) {
            log.info("[HRM Seed] Bảng nhân viên không rỗng -> bỏ qua sample để tránh ghi đè dữ liệu người dùng");
            seedLogRepository.save(new HrmSeedLog(key, "Sample skipped: non-empty database", 0, "SKIPPED"));
            return;
        }

        int count = 0;
        Map<String, Employee> employeesById = new LinkedHashMap<>();
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("employees", List.of())) {
            Employee e = mapSeedEmployee(m);
            String code = e.getEmployeeCode();
            if (!employeeRepository.existsByEmployeeCode(code)) {
                employeeRepository.save(e);
                count++;
            }
            employeesById.put(code, e);
        }

        Map<String, HrmCandidate> candidatesById = new LinkedHashMap<>();
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("candidates", List.of())) {
            HrmCandidate c = HrmJson.fromMap(m, HrmCandidate.class);
            c.setStageHistory((List<Map<String, Object>>) m.get("stageHistory"));
            fillAttributes(m, c);
            if (!candidateRepository.existsById(c.getId())) {
                candidateRepository.save(c);
                count++;
            }
            candidatesById.put(c.getId(), c);
        }

        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("attendanceRecords", List.of())) {
            HrmAttendance a = HrmJson.fromMap(m, HrmAttendance.class);
            a.setEmployee(employeesById.get(str(m.get("employeeId"))));
            if (a.getEmployee() != null && !attendanceRepository.existsById(a.getId())) {
                attendanceRepository.save(a);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("leaveRequests", List.of())) {
            HrmLeaveRequest l = HrmJson.fromMap(m, HrmLeaveRequest.class);
            l.setEmployee(employeesById.get(str(m.get("employeeId"))));
            if (l.getEmployee() != null && !leaveRequestRepository.existsById(l.getId())) {
                leaveRequestRepository.save(l);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("leaveBalances", List.of())) {
            HrmLeaveBalance b = HrmJson.fromMap(m, HrmLeaveBalance.class);
            bucket(m.get("annual"), v -> b.setAnnualTotal(((Number) v).intValue()),
                    v -> b.setAnnualUsed(((Number) v).intValue()), v -> b.setAnnualPending(((Number) v).intValue()));
            bucket(m.get("sick"), v -> b.setSickTotal(((Number) v).intValue()),
                    v -> b.setSickUsed(((Number) v).intValue()), v -> b.setSickPending(((Number) v).intValue()));
            bucket(m.get("unpaid"), v -> b.setUnpaidTotal(((Number) v).intValue()),
                    v -> b.setUnpaidUsed(((Number) v).intValue()), v -> b.setUnpaidPending(((Number) v).intValue()));
            b.setEmployee(employeesById.get(str(m.get("employeeId"))));
            if (b.getEmployee() != null && !leaveBalanceRepository.existsById(b.getId())) {
                leaveBalanceRepository.save(b);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("documents", List.of())) {
            HrmDocument d = HrmJson.fromMap(m, HrmDocument.class);
            d.setEmployee(employeesById.get(str(m.get("employeeId"))));
            if (d.getEmployee() != null && !documentRepository.existsById(d.getId())) {
                documentRepository.save(d);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("activityLog", List.of())) {
            HrmActivityLog a = HrmJson.fromMap(m, HrmActivityLog.class);
            if (!activityLogRepository.existsById(a.getId())) {
                activityLogRepository.save(a);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("interviews", List.of())) {
            HrmInterview i = HrmJson.fromMap(m, HrmInterview.class);
            i.setCandidate(candidatesById.get(str(m.get("candidateId"))));
            if (i.getCandidate() != null && !interviewRepository.existsById(i.getId())) {
                interviewRepository.save(i);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("positions", List.of())) {
            HrmPosition p = HrmJson.fromMap(m, HrmPosition.class);
            if (!positionRepository.existsById(p.getId())) {
                positionRepository.save(p);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("goals", List.of())) {
            HrmGoal g = HrmJson.fromMap(m, HrmGoal.class);
            if (!goalRepository.existsById(g.getId())) {
                goalRepository.save(g);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("kpis", List.of())) {
            HrmKpi k = HrmJson.fromMap(m, HrmKpi.class);
            fillAttributes(m, k);
            if (!kpiRepository.existsById(k.getId())) {
                kpiRepository.save(k);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("reviews", List.of())) {
            HrmReview r = HrmJson.fromMap(m, HrmReview.class);
            fillAttributes(m, r);
            if (!reviewRepository.existsById(r.getId())) {
                reviewRepository.save(r);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("feedback", List.of())) {
            HrmFeedback f = HrmJson.fromMap(m, HrmFeedback.class);
            fillAttributes(m, f);
            if (!feedbackRepository.existsById(f.getId())) {
                feedbackRepository.save(f);
                count++;
            }
        }
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("notifications", List.of())) {
            HrmNotification n = HrmJson.fromMap(m, HrmNotification.class);
            if (!notificationRepository.existsById(n.getId())) {
                notificationRepository.save(n);
                count++;
            }
        }
        int idx = 1;
        for (Map<String, Object> m : (List<Map<String, Object>>) seed.getOrDefault("growth", List.of())) {
            HrmGrowth g = HrmJson.fromMap(m, HrmGrowth.class);
            if (g.getId() == null) g.setId("GRW-" + String.format("%03d", idx));
            if (!growthRepository.existsById(g.getId())) {
                growthRepository.save(g);
                count++;
            }
            idx++;
        }

        seedLogRepository.save(new HrmSeedLog(key, "Business sample seed", count, "IMPORTED"));
        log.info("[HRM Seed] Sample '{}' import thành công: {} bản ghi", key, count);
    }

    /** Map bản ghi employees trong hrm-seed.json sang entity Employee duy nhất. */
    private Employee mapSeedEmployee(Map<String, Object> m) {
        Employee e = new Employee();
        e.setEmployeeCode(str(m.get("id")));
        e.setFirstName(str(m.get("firstName")));
        e.setLastName(str(m.get("lastName")));
        e.setName(str(m.get("name")));
        e.setInitials(str(m.get("initials")));
        e.setPosition(str(m.get("position")));
        e.setDepartmentName(str(m.get("department")));
        e.setManager(str(m.get("manager")));
        e.setEmploymentType(str(m.get("employmentType")));
        e.setStatus(str(m.get("status")));
        e.setJoinedDateIso(str(m.get("joinedDateIso")));
        e.setJoined_date(str(m.get("joinedDate")));
        e.setEmail(str(m.get("email")));
        e.setPhone_number(str(m.get("phone")));
        e.setBirth_date(str(m.get("dateOfBirth")));
        e.setAddress(str(m.get("address")));
        e.setLocation(str(m.get("location")));
        e.setIdCard(str(m.get("idCard")));
        e.setBankAccount(str(m.get("bankAccount")));
        e.setProbationEndDate(str(m.get("probationEndDate")));
        e.setAvatar(str(m.get("avatar")));
        e.setAttendanceRate(intOf(m.get("attendanceRate"), 0));
        e.setLeaveBalance(intOf(m.get("leaveBalance"), 20));
        e.setGoals(intOf(m.get("goals"), 0));
        if (m.get("emergencyContact") instanceof Map<?, ?> ec) {
            e.setEmergencyName(str(ec.get("name")));
            e.setEmergencyRelation(str(ec.get("relation")));
            e.setEmergencyPhone(str(ec.get("phone")));
        }
        if ((e.getName() == null || e.getName().isBlank()) && (e.getFirstName() != null || e.getLastName() != null)) {
            e.setName((nvl(e.getFirstName()) + " " + nvl(e.getLastName())).trim());
        }
        return e;
    }

    private int intOf(Object value, int fallback) {
        if (value instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(value)); } catch (Exception ex) { return fallback; }
    }

    private <T extends HrmAttributesAware> T fillAttributes(Map<String, Object> source, T entity) {
        Set<String> known = new HashSet<>(HrmJson.mapper().convertValue(entity,
                new TypeReference<Map<String, Object>>() {}).keySet());
        known.add("attributes");
        Map<String, Object> attrs = new LinkedHashMap<>();
        source.forEach((k, v) -> {
            if (!known.contains(k)) attrs.put(k, v);
        });
        entity.setAttributes(attrs);
        return entity;
    }

    private void bucket(Object raw, java.util.function.Consumer<Object> total,
                        java.util.function.Consumer<Object> used, java.util.function.Consumer<Object> pending) {
        if (!(raw instanceof Map<?, ?> m)) return;
        if (m.get("total") instanceof Number) total.accept(m.get("total"));
        if (m.get("used") instanceof Number) used.accept(m.get("used"));
        if (m.get("pending") instanceof Number) pending.accept(m.get("pending"));
    }

    private static String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }
}
