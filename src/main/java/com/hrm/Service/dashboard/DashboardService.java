package com.hrm.Service.dashboard;

import com.hrm.dto.response.dashboard.DashboardResponse;
import com.hrm.repository.hrm.HrmLeaveRequestRepository;
import com.hrm.repository.job.DisqualifiedRepository;
import com.hrm.repository.office.DepartmentRepository;
import com.hrm.repository.timekeeping.TimeRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.AdvanceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {
    EmployeeRepository employeeRepository;
    TimeRepository timeRepository;
    HrmLeaveRequestRepository leaveRequestRepository;
    AdvanceRepository advanceRepository;
    DisqualifiedRepository disqualifiedRepository;
    DepartmentRepository departmentRepository;

    // thống kê tổng quan cho dashboard
    public DashboardResponse getStats() {
        String today = LocalDate.now().toString();
        return DashboardResponse.builder()
                .totalEmployees(employeeRepository.count())
                .presentToday(timeRepository.countByDate(today))
                .pendingLeaves(leaveRequestRepository.countByStatus("Pending"))
                .pendingAdvances(advanceRepository.countByStatus(0))
                .pendingCvs(disqualifiedRepository.countByStatus(0))
                .totalDepartments(departmentRepository.countByStatus(1))
                .build();
    }
}