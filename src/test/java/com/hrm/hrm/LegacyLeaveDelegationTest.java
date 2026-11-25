package com.hrm.hrm;

import com.hrm.Entity.day_off.DayOffCategories;
import com.hrm.Entity.hrm.HrmLeaveRequest;
import com.hrm.Service.day_off.LeaveService;
import com.hrm.Service.hrm.HrmService;
import com.hrm.dto.request.leave.LeaveRequest;
import com.hrm.dto.request.leave.LeaveUpdateRequest;
import com.hrm.dto.response.day_off.LeaveRespone;
import com.hrm.repository.day_off.DayOffRepository;
import com.hrm.repository.hrm.HrmLeaveRequestRepository;
import com.hrm.repository.user.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** API cũ /api/day_off_letter và API HRM phải dùng cùng 1 bảng đơn nghỉ. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LegacyLeaveDelegationTest {

    @Autowired LeaveService legacyLeaveService;
    @Autowired HrmService hrmService;
    @Autowired HrmLeaveRequestRepository leaveRequestRepository;
    @Autowired DayOffRepository dayOffRepository;
    @Autowired EmployeeRepository employeeRepository;

    @Test
    void legacyCreateAndApprove_useHrmLeaveRequestTable() {
        Map<String, Object> emp = hrmService.createEmployee(Map.of(
                "firstName", "Legacy", "lastName", "Leave", "email", "legacy-leave@test.io",
                "department", "Engineering", "joinDate", "2026-03-01"));
        String hrmId = String.valueOf(emp.get("id"));
        int employeeDbId = employeeRepository.findByEmployeeCode(hrmId).orElseThrow().getId();

        DayOffCategories category = dayOffRepository.save(DayOffCategories.builder()
                .nameDay("Annual Leave").timeDay(8).status(1).build());

        long before = leaveRequestRepository.count();

        LeaveRequest request = new LeaveRequest();
        request.setDayOff(category.getId());
        request.setStartTime("2026-12-01 08:00");
        request.setEndTime("2026-12-02 17:00");
        request.setTotalTime("16");
        request.setEmployeeId(employeeDbId);
        request.setReason("Legacy API test");

        LeaveRespone created = legacyLeaveService.createLeave(request);
        assertThat(leaveRequestRepository.count()).isEqualTo(before + 1);
        assertThat(created.getId()).isPositive();
        assertThat(created.getStatus()).isZero();

        LeaveUpdateRequest approval = new LeaveUpdateRequest();
        approval.setStatus(1);
        approval.setNameApproval("Manager");
        approval.setTime(16);
        approval.setEmployeeId(employeeDbId);

        legacyLeaveService.updateStatus(created.getId(), approval);

        HrmLeaveRequest canonical = leaveRequestRepository.findByLegacyId(created.getId()).orElseThrow();
        assertThat(canonical.getStatus()).isEqualTo("Approved");
        assertThat(canonical.getApprover()).isEqualTo("Manager");
        assertThat(legacyLeaveService.getLeave(created.getId()).getStatus()).isEqualTo(1);
        assertThat(hrmService.getLeaveBalances(hrmId).get(0).get("used")).isNotNull();
    }
}
