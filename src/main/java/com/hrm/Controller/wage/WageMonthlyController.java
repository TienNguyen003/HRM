package com.hrm.Controller.wage;

import com.hrm.Service.wage.WageMonthlyService;
import com.hrm.dto.request.wage.salaryDynamicValues.WageMonthlyRequest;
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
    ApiResponse<WageMonthlyRespone> create(@RequestBody WageMonthlyRequest request){
        return ApiResponse.<WageMonthlyRespone>builder()
                .result(wageService.create(request))
                .build();
    }

    @GetMapping("/wage")
    ApiResponse<List<WageMonthlyRespone>> getWage(@RequestParam int wageIdEmployee){
        return ApiResponse.<List<WageMonthlyRespone>>builder()
                .result(wageService.getWage(wageIdEmployee))
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
    ApiResponse<WageMonthlyRespone> update(@RequestParam int wageId, @RequestBody WageMonthlyRequest request){
        return ApiResponse.<WageMonthlyRespone>builder()
                .result(wageService.update(wageId, request))
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
