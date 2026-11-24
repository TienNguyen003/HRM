package com.hrm.hrm;

import com.hrm.Exception.AppException;
import com.hrm.Service.hrm.HrmService;
import com.hrm.repository.hrm.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Kiểm thử nghiệp vụ, workflow, validation, transaction và tính nhất quán dữ liệu. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HrmServiceBusinessTest {

    @Autowired HrmService service;
    @Autowired HrmEmployeeRepository employeeRepository;
    @Autowired HrmAttendanceRepository attendanceRepository;
    @Autowired HrmLeaveBalanceRepository leaveBalanceRepository;
    @Autowired HrmLeaveRequestRepository leaveRequestRepository;
    @Autowired HrmAuditLogRepository auditLogRepository;
    @Autowired HrmPayslipRepository payslipRepository;

    private Map<String, Object> employeePayload(String email) {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("firstName", "Test");
        p.put("lastName", "Employee");
        p.put("email", email);
        p.put("department", "Engineering");
        p.put("position", "QA Engineer");
        p.put("joinDate", "2026-01-15");
        return p;
    }

    @Test
    void createEmployee_persistsToDb_withAttendanceAndBalance() {
        Map<String, Object> created = service.createEmployee(employeePayload("qa@test.io"));
        String id = String.valueOf(created.get("id"));

        assertThat(id).matches("EMP-\\d{5}");
        assertThat(employeeRepository.findById(id)).isPresent();
        assertThat(attendanceRepository.findAllByEmployee_Id(id)).hasSize(1);
        assertThat(leaveBalanceRepository.findByEmployee_Id(id)).hasSize(1);
        assertThat(service.isEmployeeIdAvailable(id)).isFalse();
        assertThat(service.isEmployeeIdAvailable("EMP-99999")).isTrue();
    }

    @Test
    void createEmployee_withDuplicateExplicitId_rejectsOrGeneratesNew() {
        Map<String, Object> first = employeePayload("dup@test.io");
        first.put("employeeId", "EMP-00500");
        service.createEmployee(first);

        Map<String, Object> second = employeePayload("dup2@test.io");
        second.put("employeeId", "EMP-00500");
        Map<String, Object> created = service.createEmployee(second);
        assertThat(created.get("id")).isNotEqualTo("EMP-00500");
    }

    @Test
    void leaveWorkflow_updatesBalanceAccordingToStateTransitions() {
        Map<String, Object> emp = service.createEmployee(employeePayload("leave@test.io"));
        String empId = String.valueOf(emp.get("id"));

        Map<String, Object> balanceBefore = service.getLeaveBalances(empId).get(0);
        int annualBefore = ((Number) ((Map<?, ?>) balanceBefore.get("annual")).get("total")).intValue();

        Map<String, Object> leave = new LinkedHashMap<>();
        leave.put("employeeId", empId);
        leave.put("type", "Annual Leave");
        leave.put("start", "2026-12-01");
        leave.put("end", "2026-12-03");
        leave.put("days", 3);
        leave.put("reason", "Test");
        Map<String, Object> request = service.createLeaveRequest(leave);
        String leaveId = String.valueOf(request.get("id"));

        Map<String, Object> pending = service.getLeaveBalances(empId).get(0);
        assertThat(((Map<?, ?>) pending.get("annual")).get("pending")).isEqualTo(3);

        service.updateLeaveStatus(leaveId, "Approved", "Manager");

        Map<String, Object> approved = service.getLeaveBalances(empId).get(0);
        assertThat(((Map<?, ?>) approved.get("annual")).get("pending")).isEqualTo(0);
        assertThat(((Map<?, ?>) approved.get("annual")).get("used")).isEqualTo(3);
        assertThat(annualBefore).isEqualTo(20);

        assertThat(service.getLeaveById(leaveId).get("timeline")).isNotNull();
    }

    @Test
    void invalidInputs_areRejected() {
        assertThatThrownBy(() -> service.getEmployeeById("EMP-DOES-NOT-EXIST"))
                .isInstanceOf(AppException.class);

        Map<String, Object> badLeave = new LinkedHashMap<>();
        badLeave.put("employeeId", "EMP-00124");
        badLeave.put("start", "not-a-date");
        badLeave.put("end", "2026-12-03");
        badLeave.put("days", 1);
        assertThatThrownBy(() -> service.createLeaveRequest(badLeave)).isInstanceOf(AppException.class);

        badLeave.put("start", "2026-12-05");
        assertThatThrownBy(() -> service.createLeaveRequest(badLeave)).isInstanceOf(AppException.class);

        badLeave.put("start", "2026-12-01");
        badLeave.put("days", 0);
        assertThatThrownBy(() -> service.createLeaveRequest(badLeave)).isInstanceOf(AppException.class);
    }

    @Test
    void deleteEmployee_cascadesRelatedData() {
        Map<String, Object> emp = service.createEmployee(employeePayload("delete@test.io"));
        String id = String.valueOf(emp.get("id"));
        service.deleteEmployee(id);

        assertThat(employeeRepository.findById(id)).isEmpty();
        assertThat(attendanceRepository.findAllByEmployee_Id(id)).isEmpty();
        assertThat(leaveBalanceRepository.findByEmployee_Id(id)).isEmpty();
    }

    @Test
    void payrollApproval_generatesPayslipsAndIsIdempotent() {
        Map<String, Object> run = new LinkedHashMap<>();
        run.put("month", "2026-11");
        run.put("amount", 1000);
        String runId = String.valueOf(service.createPayrollRun(run).get("id"));

        service.updatePayrollRunStatus(runId, "Approved");
        long afterFirst = payslipRepository.count();
        assertThat(afterFirst).isGreaterThan(0);

        service.updatePayrollRunStatus(runId, "Approved");
        assertThat(payslipRepository.count()).isEqualTo(afterFirst);
    }

    @Test
    void auditLog_recordsBusinessOperations() {
        long before = auditLogRepository.count();
        Map<String, Object> created = service.createEmployee(employeePayload("audit@test.io"));
        service.updateEmployee(String.valueOf(created.get("id")), Map.of("position", "Lead QA"));

        assertThat(auditLogRepository.count()).isGreaterThan(before);
        assertThat(service.getAuditLogs()).isNotEmpty();
    }

    @Test
    void checkIn_thenCheckOut_updatesSameRecord() {
        Map<String, Object> created = service.createEmployee(employeePayload("att@test.io"));
        String id = String.valueOf(created.get("id"));

        Map<String, Object> in = service.checkIn(id);
        assertThat(in.get("checkOut")).isEqualTo("--");
        service.checkOut(id);
        assertThat(attendanceRepository.findAllByEmployee_Id(id))
                .anySatisfy(a -> assertThat(a.getCheckOut()).isNotEqualTo("--"));
    }
}
