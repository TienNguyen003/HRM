package com.hrm.Controller.hrm;

import com.hrm.Service.hrm.HrmService;
import com.hrm.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}hrm")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HrmController {

    HrmService hrmService;

    // metadata
    @GetMapping("/filter-options")
    @PreAuthorize("@requiredPermission.checkPermission('EMP_VIEW')")
    ApiResponse<Map<String, Object>> getFilterOptions() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getFilterOptions()).build();
    }
    // employee
    @PreAuthorize("@requiredPermission.checkPermission('EMP_VIEW')")
    @GetMapping("/employees")
    ApiResponse<Map<String, Object>> getEmployeeDirectory(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getEmployeeDirectory(search, department, status, position, employmentType, location, sort, sortDirection, page, pageSize)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('EMP_VIEW')")
    @GetMapping("/employees/summary")
    ApiResponse<Map<String, Object>> getEmployeeSummary() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getEmployeeSummary()).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('EMP_VIEW')")
    @GetMapping("/employees/check-id")
    ApiResponse<Boolean> isEmployeeIdAvailable(@RequestParam String id) {
        return ApiResponse.<Boolean>builder().result(hrmService.isEmployeeIdAvailable(id)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('EMP_VIEW') or @requiredPermission.checkPermission('SELF_VIEW')")
    @GetMapping("/employees/{id}")
    ApiResponse<Map<String, Object>> getEmployeeById(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getEmployeeById(id)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('EMP_ADD')")
    @PostMapping("/employees")
    ApiResponse<Map<String, Object>> createEmployee(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createEmployee(payload)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('EMP_EDIT')")
    @PutMapping("/employees/{id}")
    ApiResponse<Map<String, Object>> updateEmployee(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateEmployee(id, payload)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('EMP_DELETE')")
    @DeleteMapping("/employees/{id}")
    ApiResponse<Map<String, Object>> deleteEmployee(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.deleteEmployee(id)).build();
    }
    // organization
    @PreAuthorize("@requiredPermission.checkPermission('ORG_VIEW')")
    @GetMapping("/organization/overview")
    ApiResponse<Map<String, Object>> getOrganizationOverview() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getOrganizationOverview()).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ORG_VIEW')")
    @GetMapping("/organization/departments/{name}")
    ApiResponse<Map<String, Object>> getDepartmentDetail(@PathVariable String name) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getDepartmentDetail(name)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ORG_VIEW')")
    @GetMapping("/organization/chart")
    ApiResponse<Map<String, Object>> getOrganizationChart() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getOrganizationChart()).build();
    }
    // attendance
    @PreAuthorize("@requiredPermission.checkPermission('ATT_VIEW') or @requiredPermission.checkPermission('ATT_CHECKIN')")
    @GetMapping("/attendance")
    ApiResponse<Map<String, Object>> getAttendanceRecords(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getAttendanceRecords(date, department, employeeId, status, sort, sortDirection, page, pageSize)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ATT_VIEW')")
    @GetMapping("/attendance/summary")
    ApiResponse<Map<String, Object>> getAttendanceSummary() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getAttendanceSummary()).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ATT_VIEW')")
    @GetMapping("/attendance/calendar")
    ApiResponse<Map<String, Object>> getAttendanceCalendar(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String department) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getAttendanceCalendar(month, department)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ATT_CHECKIN')")
    @PostMapping("/attendance/check-in")
    ApiResponse<Map<String, Object>> checkIn(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.checkIn(String.valueOf(payload.get("employeeId")))).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ATT_CHECKIN')")
    @PostMapping("/attendance/check-out")
    ApiResponse<Map<String, Object>> checkOut(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.checkOut(String.valueOf(payload.get("employeeId")))).build();
    }
    // leave
    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_VIEW') or @requiredPermission.checkPermission('LEAVE_CREATE')")
    @GetMapping("/leave/requests")
    ApiResponse<Map<String, Object>> getLeaveRequests(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getLeaveRequests(search, status, type, employeeId, sort, sortDirection, page, pageSize)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_VIEW')")
    @GetMapping("/leave/summary")
    ApiResponse<Map<String, Object>> getLeaveSummary() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getLeaveSummary()).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_VIEW')")
    @GetMapping("/leave/balances")
    ApiResponse<List<Map<String, Object>>> getLeaveBalances(@RequestParam(required = false) String employeeId) {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getLeaveBalances(employeeId)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_VIEW')")
    @GetMapping("/leave/requests/{id}")
    ApiResponse<Map<String, Object>> getLeaveById(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getLeaveById(id)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_CREATE')")
    @PostMapping("/leave/requests")
    ApiResponse<Map<String, Object>> createLeaveRequest(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createLeaveRequest(payload)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_APPROVE')")
    @PutMapping("/leave/requests/{id}/status")
    ApiResponse<Map<String, Object>> updateLeaveStatus(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateLeaveStatus(id, String.valueOf(payload.get("status")), String.valueOf(payload.get("approver")))).build();
    }
    // recruitment
    @PreAuthorize("@requiredPermission.checkPermission('REC_VIEW')")
    @GetMapping("/recruitment/overview")
    ApiResponse<Map<String, Object>> getRecruitmentOverview() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getRecruitmentOverview()).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REC_VIEW')")
    @GetMapping("/recruitment/candidates/{id}")
    ApiResponse<Map<String, Object>> getCandidateById(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getCandidateById(id)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REC_EDIT')")
    @PutMapping("/recruitment/candidates/{id}/stage")
    ApiResponse<Map<String, Object>> updateCandidateStage(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateCandidateStage(id, String.valueOf(payload.get("stage")))).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REC_ADD')")
    @PostMapping("/recruitment/interviews")
    ApiResponse<Map<String, Object>> scheduleInterview(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.scheduleInterview(payload)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REC_ADD')")
    @PostMapping("/recruitment/positions")
    ApiResponse<Map<String, Object>> createPosition(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createPosition(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('REC_ADD')")
    @PostMapping("/recruitment/candidates")
    ApiResponse<Map<String, Object>> createCandidate(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createCandidate(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('REC_EDIT')")
    @PutMapping("/recruitment/candidates/{id}")
    ApiResponse<Map<String, Object>> updateCandidate(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateCandidate(id, payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('REC_DELETE')")
    @DeleteMapping("/recruitment/candidates/{id}")
    ApiResponse<Map<String, Object>> deleteCandidate(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.deleteCandidate(id)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('REC_EDIT')")
    @PutMapping("/recruitment/interviews/{id}")
    ApiResponse<Map<String, Object>> updateInterview(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateInterview(id, payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('REC_EDIT')")
    @PutMapping("/recruitment/interviews/{id}/cancel")
    ApiResponse<Map<String, Object>> cancelInterview(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.cancelInterview(id)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('REC_EDIT')")
    @PutMapping("/recruitment/positions/{id}")
    ApiResponse<Map<String, Object>> updatePosition(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updatePosition(id, payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('REC_EDIT')")
    @PutMapping("/recruitment/positions/{id}/close")
    ApiResponse<Map<String, Object>> closePosition(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.closePosition(id)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REC_VIEW')")
    @GetMapping("/recruitment/summary")
    ApiResponse<Map<String, Object>> getRecruitmentSummary() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getRecruitmentSummary()).build();
    }
    // performance
    @PreAuthorize("@requiredPermission.checkPermission('PERF_VIEW')")
    @GetMapping("/performance/overview")
    ApiResponse<Map<String, Object>> getPerformanceOverview() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getPerformanceOverview()).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('PERF_ADD')")
    @PostMapping("/performance/goals")
    ApiResponse<Map<String, Object>> createGoal(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createGoal(payload)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('PERF_ADD')")
    @PutMapping("/performance/goals/{id}/progress")
    ApiResponse<Map<String, Object>> updateGoalProgress(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateGoalProgress(id, payload.get("progress"))).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('PERF_VIEW')")
    @GetMapping("/performance/reviews/{id}")
    ApiResponse<Map<String, Object>> getReviewById(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getReviewById(id)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PERF_EDIT')")
    @PutMapping("/performance/reviews/{id}")
    ApiResponse<Map<String, Object>> updateReview(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateReview(id, payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PERF_EDIT')")
    @PutMapping("/performance/reviews/{id}/complete")
    ApiResponse<Map<String, Object>> completeReview(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.completeReview(id, payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PERF_VIEW')")
    @GetMapping("/performance/kpis")
    ApiResponse<List<Map<String, Object>>> getKpis() {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getKpis()).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PERF_ADD')")
    @PostMapping("/performance/kpis")
    ApiResponse<Map<String, Object>> createKpi(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createKpi(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PERF_EDIT')")
    @PutMapping("/performance/kpis/{id}")
    ApiResponse<Map<String, Object>> updateKpi(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateKpi(id, payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PERF_EDIT')")
    @DeleteMapping("/performance/kpis/{id}")
    ApiResponse<Map<String, Object>> deleteKpi(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.deleteKpi(id)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PERF_ADD')")
    @PostMapping("/performance/feedback")
    ApiResponse<Map<String, Object>> createFeedback(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createFeedback(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PERF_EDIT')")
    @PutMapping("/performance/feedback/{id}")
    ApiResponse<Map<String, Object>> updateFeedback(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateFeedback(id, payload)).build();
    }

    // documents
    @PreAuthorize("@requiredPermission.checkPermission('EMP_EDIT')")
    @PostMapping("/employees/{id}/documents")
    ApiResponse<Map<String, Object>> addEmployeeDocument(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.addEmployeeDocument(id, payload)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('EMP_EDIT')")
    @PostMapping(value = "/employees/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Map<String, Object>> uploadEmployeeDocument(@PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String expires) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", name == null || name.isBlank() ? file.getOriginalFilename() : name);
        payload.put("type", type == null || type.isBlank() ? "Other" : type);
        payload.put("size", file.getSize() + " bytes");
        payload.put("expires", expires);
        payload.put("fileName", file.getOriginalFilename());
        payload.put("contentType", file.getContentType());
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.addEmployeeDocument(id, payload)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('EMP_EDIT')")
    @DeleteMapping("/employees/{employeeId}/documents/{documentId}")
    ApiResponse<Map<String, Object>> removeEmployeeDocument(@PathVariable String employeeId, @PathVariable String documentId) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.removeEmployeeDocument(documentId)).build();
    }

    // notifications (dữ liệu đã được service lọc theo người đăng nhập)
    @PreAuthorize("@requiredPermission.checkPermission('SELF_VIEW')")
    @GetMapping("/notifications")
    ApiResponse<List<Map<String, Object>>> getNotifications() {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getNotifications()).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('SELF_VIEW')")
    @GetMapping("/notifications/unread-count")
    ApiResponse<Long> getUnreadNotificationCount() {
        return ApiResponse.<Long>builder().result(hrmService.getUnreadNotificationCount()).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('SELF_VIEW')")
    @PutMapping("/notifications/{id}/read")
    ApiResponse<Map<String, Object>> markNotificationRead(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.markNotificationRead(id)).build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('SELF_VIEW')")
    @PutMapping("/notifications/read-all")
    ApiResponse<List<Map<String, Object>>> markAllNotificationsRead() {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.markAllNotificationsRead()).build();
    }

    // dashboard
    @PreAuthorize("@requiredPermission.checkPermission('ORG_VIEW')")
    @GetMapping("/dashboard/overview")
    ApiResponse<Map<String, Object>> getDashboardOverview() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getDashboardOverview()).build();
    }

    // reports
    @PreAuthorize("@requiredPermission.checkPermission('ORG_VIEW')")
    @GetMapping("/reports/overview")
    ApiResponse<Map<String, Object>> getReportsOverview() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getReportsOverview()).build();
    }

    // overtime
    @PreAuthorize("@requiredPermission.checkPermission('OT_VIEW')")
    @GetMapping("/overtime")
    ApiResponse<Map<String, Object>> getOvertime(@RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getOvertime(employeeId, department, status, page, pageSize)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('OT_VIEW')")
    @GetMapping("/overtime/summary")
    ApiResponse<Map<String, Object>> getOvertimeSummary() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getOvertimeSummary()).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('OT_CREATE')")
    @PostMapping("/overtime")
    ApiResponse<Map<String, Object>> createOvertime(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createOvertime(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('OT_APPROVE')")
    @PutMapping("/overtime/{id}/status")
    ApiResponse<Map<String, Object>> updateOvertimeStatus(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateOvertimeStatus(id, String.valueOf(payload.get("status")))).build();
    }

    // payroll runs
    @PreAuthorize("@requiredPermission.checkPermission('PAYROLL_VIEW')")
    @GetMapping("/payroll-runs")
    ApiResponse<Map<String, Object>> getPayrollRuns(@RequestParam(required = false) String month,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getPayrollRuns(month, department, status, page, pageSize)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PAYROLL_RUN')")
    @PostMapping("/payroll-runs")
    ApiResponse<Map<String, Object>> createPayrollRun(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createPayrollRun(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PAYROLL_VIEW')")
    @GetMapping("/payroll-runs/{id}")
    ApiResponse<Map<String, Object>> getPayrollRunById(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getPayrollRunById(id)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PAYROLL_RUN')")
    @PutMapping("/payroll-runs/{id}/status")
    ApiResponse<Map<String, Object>> updatePayrollRunStatus(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updatePayrollRunStatus(id, String.valueOf(payload.get("status")))).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PAYROLL_RUN')")
    @PostMapping("/payroll-runs/{id}/recalculate")
    ApiResponse<Map<String, Object>> recalculatePayrollRun(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.recalculatePayrollRun(id)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PAYROLL_VIEW')")
    @GetMapping("/payroll-table")
    ApiResponse<Map<String, Object>> getPayrollTable(@RequestParam(required = false) String month,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getPayrollTable(month, department, page, pageSize)).build();
    }
    // payslips
    @PreAuthorize("@requiredPermission.checkPermission('PAYSLIP_VIEW')")
    @GetMapping("/payslips")
    ApiResponse<Map<String, Object>> getPayslips(@RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getPayslips(employeeId, month, page, pageSize)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('PAYSLIP_VIEW')")
    @GetMapping("/payslips/{id}")
    ApiResponse<Map<String, Object>> getPayslipById(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getPayslipById(id)).build();
    }

    // onboarding / offboarding
    @PreAuthorize("@requiredPermission.checkPermission('ONBOARDING_VIEW')")
    @GetMapping("/onboarding")
    ApiResponse<Map<String, Object>> getOnboarding(@RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getOnboarding(employeeId, status, page, pageSize)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('ONBOARDING_MANAGE')")
    @PostMapping("/onboarding")
    ApiResponse<Map<String, Object>> createOnboarding(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createOnboarding(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('ONBOARDING_MANAGE')")
    @PutMapping("/onboarding/{id}/status")
    ApiResponse<Map<String, Object>> updateOnboardingStatus(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateOnboardingStatus(id, String.valueOf(payload.get("status")))).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('ONBOARDING_VIEW')")
    @GetMapping("/offboarding")
    ApiResponse<Map<String, Object>> getOffboarding(@RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getOffboarding(employeeId, status, page, pageSize)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('ONBOARDING_MANAGE')")
    @PostMapping("/offboarding")
    ApiResponse<Map<String, Object>> createOffboarding(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createOffboarding(payload)).build();
    }

    // leave policies
    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_POLICY_VIEW')")
    @GetMapping("/leave-policies")
    ApiResponse<List<Map<String, Object>>> getLeavePolicies() {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getLeavePolicies()).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_POLICY_MANAGE')")
    @PostMapping("/leave-policies")
    ApiResponse<Map<String, Object>> createLeavePolicy(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createLeavePolicy(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_POLICY_MANAGE')")
    @PutMapping("/leave-policies/{id}")
    ApiResponse<Map<String, Object>> updateLeavePolicy(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateLeavePolicy(id, payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('LEAVE_POLICY_MANAGE')")
    @PostMapping("/leave/accrual/run")
    ApiResponse<Map<String, Object>> runLeaveAccrual() {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.runLeaveAccrual()).build();
    }

    // shift scheduling
    @PreAuthorize("@requiredPermission.checkPermission('SHIFT_VIEW')")
    @GetMapping("/shift-schedules")
    ApiResponse<List<Map<String, Object>>> getShiftSchedules() {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getShiftSchedules()).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('SHIFT_MANAGE')")
    @PostMapping("/shift-schedules")
    ApiResponse<Map<String, Object>> createShiftSchedule(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createShiftSchedule(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('SHIFT_MANAGE')")
    @PutMapping("/shift-schedules/{id}")
    ApiResponse<Map<String, Object>> updateShiftSchedule(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateShiftSchedule(id, payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('SHIFT_VIEW')")
    @GetMapping("/rosters")
    ApiResponse<Map<String, Object>> getRosters(@RequestParam(required = false) String month,
            @RequestParam(required = false) String department) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getRosters(month, department)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('SHIFT_MANAGE')")
    @PostMapping("/rosters/assign")
    ApiResponse<Map<String, Object>> assignRoster(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.assignRoster(payload)).build();
    }

    // benefits
    @PreAuthorize("@requiredPermission.checkPermission('BENEFIT_VIEW')")
    @GetMapping("/benefits")
    ApiResponse<List<Map<String, Object>>> getBenefits() {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getBenefits()).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('BENEFIT_MANAGE')")
    @PostMapping("/benefits")
    ApiResponse<Map<String, Object>> createBenefit(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createBenefit(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('BENEFIT_MANAGE')")
    @PutMapping("/benefits/{id}")
    ApiResponse<Map<String, Object>> updateBenefit(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateBenefit(id, payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('BENEFIT_VIEW')")
    @GetMapping("/benefit-enrollments")
    ApiResponse<Map<String, Object>> getBenefitEnrollments(@RequestParam(required = false) String employeeId) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getBenefitEnrollments(employeeId)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('BENEFIT_MANAGE')")
    @PostMapping("/benefit-enrollments")
    ApiResponse<Map<String, Object>> createBenefitEnrollment(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createBenefitEnrollment(payload)).build();
    }

    // hr requests
    @PreAuthorize("@requiredPermission.checkPermission('HR_REQUEST_VIEW')")
    @GetMapping("/hr-requests")
    ApiResponse<Map<String, Object>> getHrRequests(@RequestParam(required = false) String status,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getHrRequests(status, employeeId, page, pageSize)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('HR_REQUEST_VIEW')")
    @GetMapping("/hr-requests/{id}")
    ApiResponse<Map<String, Object>> getHrRequestById(@PathVariable String id) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.getHrRequestById(id)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('HR_REQUEST_CREATE')")
    @PostMapping("/hr-requests")
    ApiResponse<Map<String, Object>> createHrRequest(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createHrRequest(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('HR_REQUEST_MANAGE')")
    @PutMapping("/hr-requests/{id}/status")
    ApiResponse<Map<String, Object>> updateHrRequestStatus(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.updateHrRequestStatus(id, String.valueOf(payload.get("status")))).build();
    }

    // announcements / events / birthdays
    @PreAuthorize("@requiredPermission.checkPermission('SELF_VIEW')")
    @GetMapping("/announcements")
    ApiResponse<List<Map<String, Object>>> getAnnouncements() {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getAnnouncements()).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('ORG_MANAGE')")
    @PostMapping("/announcements")
    ApiResponse<Map<String, Object>> createAnnouncement(@RequestBody Map<String, Object> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.createAnnouncement(payload)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('SELF_VIEW')")
    @GetMapping("/events")
    ApiResponse<List<Map<String, Object>>> getCompanyEvents(@RequestParam(required = false) String month) {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getCompanyEvents(month)).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('SELF_VIEW')")
    @GetMapping("/birthdays")
    ApiResponse<List<Map<String, Object>>> getBirthdays(@RequestParam(required = false) String month) {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getBirthdays(month)).build();
    }

    // audit logs
    @PreAuthorize("@requiredPermission.checkPermission('ORG_VIEW')")
    @GetMapping("/audit-logs")
    ApiResponse<List<Map<String, Object>>> getAuditLogs() {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.getAuditLogs()).build();
    }

    // import / export
    @PreAuthorize("@requiredPermission.checkPermission('EMP_VIEW')")
    @GetMapping("/employees/export")
    ApiResponse<List<Map<String, Object>>> exportEmployees() {
        return ApiResponse.<List<Map<String, Object>>>builder().result(hrmService.exportEmployees()).build();
    }
    @PreAuthorize("@requiredPermission.checkPermission('EMP_ADD')")
    @PostMapping("/employees/import")
    ApiResponse<Map<String, Object>> importEmployees(@RequestBody List<Map<String, Object>> payload) {
        return ApiResponse.<Map<String, Object>>builder().result(hrmService.importEmployees(payload)).build();
    }
}
