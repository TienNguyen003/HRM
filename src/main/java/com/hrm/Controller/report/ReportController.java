package com.hrm.Controller.report;

import com.hrm.Service.report.ReportService;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.report.AttendanceReport;
import com.hrm.dto.response.report.LeaveReport;
import com.hrm.dto.response.report.PayrollReport;
import com.hrm.dto.response.report.SummaryReport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
    ReportService reportService;

    @PreAuthorize("@requiredPermission.checkPermission('REPORT_VIEW')")
    @GetMapping
    ApiResponse<SummaryReport> generateAll() {
        return ApiResponse.<SummaryReport>builder()
                .result(reportService.generateSummary())
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('PAYROLL_VIEW')")
    @GetMapping("/payroll")
    ApiResponse<PayrollReport> generatePayroll() {
        return ApiResponse.<PayrollReport>builder()
                .result(reportService.generatePayroll())
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ATT_VIEW')")
    @GetMapping("/attendance")
    ApiResponse<AttendanceReport> generateAttendance() {
        return ApiResponse.<AttendanceReport>builder()
                .result(reportService.generateAttendance())
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_VIEW')")
    @GetMapping("/leave")
    ApiResponse<LeaveReport> generateLeave() {
        return ApiResponse.<LeaveReport>builder()
                .result(reportService.generateLeave())
                .build();
    }
}
