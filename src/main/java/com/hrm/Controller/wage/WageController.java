package com.hrm.Controller.wage;

import com.hrm.Service.wage.WageService;
import com.hrm.dto.request.wage.salaryStaticValue.WageRequest;
import com.hrm.dto.request.wage.salaryStaticValue.WageUpdateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.wage.WageRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}salary_static_values")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WageController {
    WageService wageService;

    @PostMapping
    ApiResponse<List<WageRespone>> create(@RequestBody List<WageRequest> request){
        return ApiResponse.<List<WageRespone>>builder()
                .result(wageService.create(request))
                .build();
    }

    @GetMapping("/wage")
    ApiResponse<List<WageRespone>> getWage(@RequestParam int employeeId){
        return ApiResponse.<List<WageRespone>>builder()
                .result(wageService.getWage(employeeId))
                .build();
    }

    @GetMapping
    ApiResponse<List<WageRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                          @RequestParam(name = "name", required = false) String name,
                                          @RequestParam(name = "wageCategories", required = false) Integer wageCategories,
                                          @RequestParam(name = "type", required = false) String type){
        return ApiResponse.<List<WageRespone>>builder()
                .result(wageService.searchAll(pageNumber, 30, name, wageCategories, type))
                .page(wageService.getPagination(pageNumber, 30, name, wageCategories, type))
                .build();
    }

    @PutMapping
    ApiResponse<List<WageRespone>> update(@RequestBody List<WageUpdateRequest> request){
        return ApiResponse.<List<WageRespone>>builder()
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
