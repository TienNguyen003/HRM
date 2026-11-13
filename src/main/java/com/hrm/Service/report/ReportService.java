package com.hrm.Service.report;

import com.hrm.Entity.wage.Payroll;
import com.hrm.dto.response.report.AttendanceReport;
import com.hrm.dto.response.report.LeaveReport;
import com.hrm.dto.response.report.PayrollReport;
import com.hrm.dto.response.report.SummaryReport;
import com.hrm.repository.day_off.LeaveRepository;
import com.hrm.repository.job.JobPostingRepository;
import com.hrm.repository.timekeeping.TimeRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.PayrollRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportService {
    EmployeeRepository employeeRepository;
    TimeRepository timeRepository;
    LeaveRepository leaveRepository;
    PayrollRepository payrollRepository;
    JobPostingRepository jobPostingRepository;

    static final int LEAVE_PENDING = 0;
    static final int LEAVE_APPROVED = 1;
    static final int LEAVE_REJECTED = 2;
    static final int JOB_OPENING = 0;

    // báo cáo lương: tổng lương, số nhân viên, lương trung bình
    public PayrollReport generatePayroll() {
        List<Payroll> payrolls = payrollRepository.findAll();
        double totalPayroll = payrolls.stream()
                .mapToDouble(payroll -> parseNumber(payroll.getTotalSalary()))
                .sum();
        long totalEmployees = employeeRepository.count();

        return PayrollReport.builder()
                .title("Báo cáo lương")
                .totalEmployees(totalEmployees)
                .totalPayroll(totalPayroll)
                .averageSalary(totalEmployees == 0 ? 0 : totalPayroll / totalEmployees)
                .generatedAt(now())
                .build();
    }

    // báo cáo chấm công: tổng lượt chấm công, số nhân viên có mặt hôm nay
    public AttendanceReport generateAttendance() {
        String today = LocalDate.now().toString();
        long totalEmployees = employeeRepository.count();
        int presentToday = timeRepository.countByDate(today);

        return AttendanceReport.builder()
                .title("Báo cáo chấm công")
                .totalAttendanceTimes(timeRepository.count())
                .presentToday(presentToday)
                .absentToday(Math.max(totalEmployees - presentToday, 0))
                .generatedAt(now())
                .build();
    }

    // báo cáo nghỉ phép: tổng đơn, số đơn theo trạng thái, tổng ngày nghỉ đã duyệt
    public LeaveReport generateLeave() {
        double totalLeaveDays = leaveRepository.findAll().stream()
                .filter(leave -> leave.getStatus() == LEAVE_APPROVED)
                .mapToDouble(leave -> parseNumber(leave.getTotalTime()))
                .sum();

        return LeaveReport.builder()
                .title("Báo cáo nghỉ phép")
                .totalLeaves(leaveRepository.count())
                .pendingLeaves(leaveRepository.countByStatus(LEAVE_PENDING))
                .approvedLeaves(leaveRepository.countByStatus(LEAVE_APPROVED))
                .rejectedLeaves(leaveRepository.countByStatus(LEAVE_REJECTED))
                .totalLeaveDays(totalLeaveDays)
                .generatedAt(now())
                .build();
    }

    // báo cáo tổng hợp tất cả chỉ số
    public SummaryReport generateSummary() {
        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("payroll", generatePayroll());
        statistics.put("attendance", generateAttendance());
        statistics.put("leave", generateLeave());
        statistics.put("openJobPostings", jobPostingRepository.countByStatus(JOB_OPENING));

        return SummaryReport.builder()
                .title("Báo cáo tổng hợp")
                .generatedAt(now())
                .statistics(statistics)
                .build();
    }

    private String now() {
        return String.valueOf(LocalDateTime.now());
    }

    private double parseNumber(String value) {
        if (value == null || value.isBlank()) return 0;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
