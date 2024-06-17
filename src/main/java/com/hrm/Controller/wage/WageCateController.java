package com.hrm.Controller.wage;

import com.hrm.Service.wage.WageCateService;
import com.hrm.dto.request.wage.WageCateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.wage.WageCateRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}salary_categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WageCateController {
    WageCateService wageCateService;

    @PostMapping
    ApiResponse<WageCateRespone> create(@RequestBody WageCateRequest request){
        return ApiResponse.<WageCateRespone>builder()
                .result(wageCateService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<WageCateRespone>> getAll(@RequestParam int pageNumber){
        return ApiResponse.<List<WageCateRespone>>builder()
                .page(String.valueOf(pageNumber))
                .result(wageCateService.getAllWageCate(pageNumber, 30))
                .build();
    }

    @GetMapping("/wages")
    ApiResponse<WageCateRespone> getWage(@RequestParam int wageCateId){
        return ApiResponse.<WageCateRespone>builder()
                .result(wageCateService.getWageCate(wageCateId))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<WageCateRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "name", required = false) String name,
                                              @RequestParam(name = "symbol", required = false) String symbol,
                                             @RequestParam(name = "salaryType", required = false)  String salaryType){
        if (name != null && name.isEmpty()) {
            name = null;
        }
        if (symbol != null && symbol.isEmpty()) {
            symbol = null;
        }
        if (salaryType != null && salaryType.isEmpty()) {
            salaryType = null;
        }
        return ApiResponse.<List<WageCateRespone>>builder()
                .result(wageCateService.searchAll(pageNumber, 30, name, symbol, salaryType))
                .build();
    }

    @PutMapping
    ApiResponse<WageCateRespone> update(@RequestParam int wageCateId, @RequestBody WageCateRequest request){
        return ApiResponse.<WageCateRespone>builder()
                .result(wageCateService.update(wageCateId, request))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int dayOffId){
        wageCateService.deleteWageCate(dayOffId);
        return ApiResponse.<String>builder()
                .result("This salary category has been deleted")
                .build();
    }
}
