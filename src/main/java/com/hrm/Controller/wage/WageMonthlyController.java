package com.hrm.Controller.wage;

import com.hrm.Service.wage.WageMonthlyService;
import com.hrm.dto.request.wage.salaryDynamicValues.WageMonthlyRequest;
import com.hrm.dto.request.wage.salaryDynamicValues.WageUpdateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.wage.WageMonthlyRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}salary_dynamic_values")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WageMonthlyController {
    WageMonthlyService wageService;

    @PostMapping
    ApiResponse<List<WageMonthlyRespone>> create(@RequestBody List<WageMonthlyRequest> request){
        return ApiResponse.<List<WageMonthlyRespone>>builder()
                .result(wageService.create(request))
                .build();
    }

    @GetMapping("/wage")
    ApiResponse<List<WageMonthlyRespone>> getWage(@RequestParam int employeeId, @RequestParam String time){
        return ApiResponse.<List<WageMonthlyRespone>>builder()
                .result(wageService.getWage(employeeId, time))
                .build();
    }

    @GetMapping
    ApiResponse<List<WageMonthlyRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                              @RequestParam(name = "name", required = false) String name,
                                                 @RequestParam(name = "time", required = false) String time,
                                              @RequestParam(name = "wageCategories", required = false) Integer wageCategories,
                                                 @RequestParam(name = "type", required = false) String type){
        return ApiResponse.<List<WageMonthlyRespone>>builder()
                .result(wageService.searchAll(pageNumber, 30, name, time, wageCategories, type))
                .page(wageService.getPagination(pageNumber, 30, name, time, wageCategories, type))
                .build();
    }

    @PutMapping
    ApiResponse<List<WageMonthlyRespone>> update(@RequestBody List<WageUpdateRequest> request){
        return ApiResponse.<List<WageMonthlyRespone>>builder()
                .result(wageService.update(request))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int wageId){
        wageService.deleteWage(wageId);
        return ApiResponse.<String>builder()
                .result("This salary has been deleted")
                .build();
    }
}
