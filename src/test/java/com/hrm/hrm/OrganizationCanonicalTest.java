package com.hrm.hrm;

import com.hrm.Entity.office.Department;
import com.hrm.Entity.office.Shift;
import com.hrm.Service.hrm.HrmService;
import com.hrm.Service.office.DepartmentService;
import com.hrm.Service.office.OfficeService;
import com.hrm.Exception.AppException;
import com.hrm.dto.request.office.DepartmentRequest;
import com.hrm.dto.request.office.OfficeRequest;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Department/Shift là nguồn đơn nhất cho cả API cũ lẫn /api/hrm. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrganizationCanonicalTest {

    @Autowired HrmService hrmService;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired ShiftRepository shiftRepository;
    @Autowired OfficeService officeService;
    @Autowired DepartmentService departmentService;

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
    void office_isCanonicalLocationAndDepartmentNameIsGloballyUnique() {
        OfficeRequest officeRequest = new OfficeRequest();
        officeRequest.setName("Da Nang Lab");
        int officeId = officeService.create(officeRequest).getId();

        java.util.List<Object> locations = new java.util.ArrayList<>(
                (java.util.List<?>) hrmService.getFilterOptions().get("locations"));
        assertThat(locations).contains("Da Nang Lab");

        Map<String, Object> created = hrmService.createEmployee(Map.of(
                "firstName", "Office", "lastName", "Tester", "email", "office@test.io",
                "department", "Lab Dept", "location", "Da Nang Lab", "joinDate", "2026-05-01"));
        String code = String.valueOf(created.get("id"));
        var employee = employeeRepository.findByEmployeeCode(code).orElseThrow();
        assertThat(employee.getOffice()).isNotNull();
        assertThat(employee.getOffice().getId()).isEqualTo(officeId);

        DepartmentRequest deptRequest = new DepartmentRequest();
        deptRequest.setName("Lab Dept");
        deptRequest.setShortName("LAB");
        deptRequest.setOfficeId(officeId);
        assertThatThrownBy(() -> departmentService.create(deptRequest)).isInstanceOf(AppException.class);
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
