package com.hrm.Controller.day_off;

import com.hrm.Entity.user.Employee;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.Service.day_off.LeaveService;
import com.hrm.dto.request.leave.LeaveRequest;
import com.hrm.dto.request.leave.LeaveUpdateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.day_off.LeaveRespone;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("${api.prefix}day_off_letter")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LeaveController {
    LeaveService leaveService;
    EmployeeRepository employeeRepository;

    @PreAuthorize("@requiredPermission.checkPermission('REQ_ADD')")
    @PostMapping
    ApiResponse<LeaveRespone> create(@RequestBody @Valid LeaveRequest leaveRequest){
        return ApiResponse.<LeaveRespone>builder()
                .result(leaveService.createLeave(leaveRequest))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REQ_EDIT')")
    @PutMapping
    ApiResponse<LeaveRespone> update(@RequestParam int leaveId,@RequestBody @Valid LeaveRequest request){
        return ApiResponse.<LeaveRespone>builder()
                .result(leaveService.updateLeave(leaveId, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REQ_APPROVALS')")
    @PutMapping("/status")
    ApiResponse<LeaveRespone> updateStt(@RequestParam int leaveId,@RequestBody @Valid LeaveUpdateRequest request){
        return ApiResponse.<LeaveRespone>builder()
                .result(leaveService.updateStatus(leaveId, request))
                .build();
    }

    @GetMapping("/leave")
    ApiResponse<LeaveRespone> getLeave(@RequestParam int leaveId){
        return ApiResponse.<LeaveRespone>builder()
                .result(leaveService.getLeave(leaveId))
                .build();
    }

    @PreAuthorize(
            "@requiredPermission.checkPermission('REQ_VIEW') or "
                    + "@requiredPermission.checkPermission('REQ_ADD') or "
                    + "@requiredPermission.checkPermission('REQ_APPROVALS')")
    @GetMapping
    ApiResponse<List<LeaveRespone>> searchAll(@RequestParam("pageNumber") int pageNumber,
                                              @RequestParam(name = "name", required = false) String name,
                                              @RequestParam(name = "status", required = false)  Integer status,
                                              @RequestParam(name = "dayOff", required = false) Integer dayOff,
                                              @RequestParam(name = "employeeId", required = false) Integer employeeId){

            return ApiResponse.<List<LeaveRespone>>builder()
                    .result(leaveService.searchAll(pageNumber, 30, name, dayOff, status, employeeId))
                    .page(leaveService.getPagination(pageNumber, 30, name, dayOff, status, employeeId))
                    .build();
    }

    @PreAuthorize(
            "@requiredPermission.checkPermission('REQ_VIEW') or "
                    + "@requiredPermission.checkPermission('REQ_APPROVALS') or "
                    + "@requiredPermission.checkPermission('ATTD_VIEW')")
    @GetMapping("/get")
    ApiResponse<List<LeaveRespone>> findAll(@RequestParam(name = " ", required = false) Integer employeeId){

        return ApiResponse.<List<LeaveRespone>>builder()
                .result(leaveService.getAll(employeeId))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REQ_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int leaveId){
        leaveService.deleteLeave(leaveId);
        return ApiResponse.<String>builder()
                .result("Day off has been deleted")
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('REQ_VIEW')")
    @GetMapping("/remaining-hours/{employeeId}")
    ApiResponse<Map<String, Integer>> getRemainingHours(@PathVariable int employeeId){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        return ApiResponse.<Map<String, Integer>>builder()
                .result(Map.of("remainingHours", employee.getVacationHours()))
                .build();
    }
}
