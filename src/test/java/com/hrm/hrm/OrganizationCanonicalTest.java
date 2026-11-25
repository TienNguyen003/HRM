package com.hrm.hrm;

import com.hrm.Entity.office.Department;
import com.hrm.Entity.office.Shift;
import com.hrm.Service.hrm.HrmService;
import com.hrm.repository.office.DepartmentRepository;
import com.hrm.repository.office.ShiftRepository;
import com.hrm.repository.user.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Department/Shift là nguồn đơn nhất cho cả API cũ lẫn /api/hrm. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrganizationCanonicalTest {

    @Autowired HrmService hrmService;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired ShiftRepository shiftRepository;

    @Test
    void createEmployee_linksToDepartmentEntity() {
        Map<String, Object> created = hrmService.createEmployee(Map.of(
                "firstName", "Org", "lastName", "Tester", "email", "org@test.io",
                "department", "QA Lab", "joinDate", "2026-04-01"));
        String code = String.valueOf(created.get("id"));

        Department dept = departmentRepository.findAll().stream()
                .filter(d -> "QA Lab".equals(d.getName())).findFirst().orElseThrow();
        var employee = employeeRepository.findByEmployeeCode(code).orElseThrow();
        assertThat(employee.getDepartment()).isNotNull();
        assertThat(employee.getDepartment().getId()).isEqualTo(dept.getId());
        assertThat(employee.getDepartmentName()).isEqualTo("QA Lab");

        Map<String, Object> overview = hrmService.getOrganizationOverview();
        assertThat((java.util.List<?>) overview.get("departments")).isNotEmpty();
        assertThat(hrmService.getDepartmentDetail("QA Lab").get("headcount")).isNotNull();
    }

    @Test
    void shiftApi_usesShiftEntity() {
        Map<String, Object> created = hrmService.createShiftSchedule(Map.of(
                "name", "Morning", "startTime", "08:00", "endTime", "17:00",
                "breakMinutes", 60, "location", "HCM"));
        String code = String.valueOf(created.get("id"));

        Shift shift = shiftRepository.findByCode(code).orElseThrow();
        assertThat(shift.getName()).isEqualTo("Morning");
        assertThat(hrmService.getShiftSchedules()).anySatisfy(m -> assertThat(m.get("id")).isEqualTo(code));

        hrmService.updateShiftSchedule(code, Map.of("name", "Early Morning"));
        assertThat(shiftRepository.findByCode(code).orElseThrow().getName()).isEqualTo("Early Morning");
    }
}
