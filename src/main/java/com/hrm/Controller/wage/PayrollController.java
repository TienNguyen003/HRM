package com.hrm.Controller.wage;

import com.hrm.Service.wage.PayrollService;
import com.hrm.dto.request.wage.payroll.PayrollEmailRequest;
import com.hrm.dto.request.wage.payroll.PayrollRequest;
import com.hrm.dto.request.wage.payroll.PayrollSttRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.wage.PayrollRespone;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}salary_tables")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayrollController {
    PayrollService payrollService;

    @PreAuthorize("@requiredPermission.checkPermission('SALA_ADD')")
    @PostMapping
    ApiResponse<PayrollRespone> create(@RequestBody @Valid PayrollRequest request){
        return ApiResponse.<PayrollRespone>builder()
                .result(payrollService.create(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('SALA_VIEW')")
    @GetMapping("/get-all")
    ApiResponse<List<PayrollRespone>> getAll(){
        return ApiResponse.<List<PayrollRespone>>builder()
                .result(payrollService.getAllPayroll())
                .build();
    }

    @GetMapping("/wage")
    ApiResponse<PayrollRespone> getWage(@RequestParam int id){
        return ApiResponse.<PayrollRespone>builder()
                .result(payrollService.getPayroll(id))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('SALA_VIEW')")
    @GetMapping
    ApiResponse<List<PayrollRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "name", required = false) String name,
                                             @RequestParam(name = "status", required = false) Integer status,
                                             @RequestParam(name = "time", required = false) String time){
        return ApiResponse.<List<PayrollRespone>>builder()
                .result(payrollService.searchAll(pageNumber, 30, name, time, status))
                .page(payrollService.getPagination(pageNumber, 30, name, time, status))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('SALA_EDIT')")
    @PutMapping
    ApiResponse<PayrollRespone> update(@RequestParam int payrollIdEmployee,@RequestBody @Valid PayrollRequest request){
        return ApiResponse.<PayrollRespone>builder()
                .result(payrollService.update(payrollIdEmployee, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('SALA_EDIT')")
    @PutMapping("payroll")
    ApiResponse<String> updateStt(@RequestBody @Valid List<PayrollSttRequest> request){
        return ApiResponse.<String>builder()
                .result(payrollService.updateStt(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('SALA_EDIT')")
    @PutMapping("email")
    ApiResponse<String> sendEmail(@RequestBody @Valid List<PayrollEmailRequest> request){
        return ApiResponse.<String>builder()
                .result(payrollService.sendEmail(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('SALA_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int payrollIdEmployee){
        payrollService.deletPayroll(payrollIdEmployee);
        return ApiResponse.<String>builder()
                .result("This salary has been deleted")
                .build();
    }
}
