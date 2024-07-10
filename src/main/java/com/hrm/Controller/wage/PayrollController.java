package com.hrm.Controller.wage;

import com.hrm.Service.wage.PayrollService;
import com.hrm.dto.request.wage.PayrollRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.wage.PayrollRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}salary_tables")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayrollController {
    PayrollService payrollService;

    @PostMapping
    ApiResponse<PayrollRespone> create(@RequestBody PayrollRequest request){
        return ApiResponse.<PayrollRespone>builder()
                .result(payrollService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PayrollRespone>> getAll(@RequestParam int pageNumber){
        return ApiResponse.<List<PayrollRespone>>builder()
                .result(payrollService.getAllPayroll(pageNumber, 30))
                .build();
    }

    @GetMapping("/wage")
    ApiResponse<PayrollRespone> getWage(@RequestParam int payrollIdEmployee){
        return ApiResponse.<PayrollRespone>builder()
                .result(payrollService.getPayroll(payrollIdEmployee))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<PayrollRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                              @RequestParam(name = "name", required = false) String name,
                                                 @RequestParam(name = "time", required = false) String time){
        return ApiResponse.<List<PayrollRespone>>builder()
                .result(payrollService.searchAll(pageNumber, 30, name, time))
                .build();
    }

    @PutMapping
    ApiResponse<PayrollRespone> update(@RequestParam int payrollIdEmployee, @RequestBody PayrollRequest request){
        return ApiResponse.<PayrollRespone>builder()
                .result(payrollService.update(payrollIdEmployee, request))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int payrollIdEmployee){
        payrollService.deletPayroll(payrollIdEmployee);
        return ApiResponse.<String>builder()
                .result("This salary has been deleted")
                .build();
    }
}
