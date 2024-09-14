package com.hrm.Controller.wage;

import com.hrm.Service.wage.WageCateService;
import com.hrm.dto.request.wage.WageCateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.wage.WageCateRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}salary_categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WageCateController {
    WageCateService wageCateService;

    @PreAuthorize("@requiredPermission.checkPermission('CATG_ADD')")
    @PostMapping
    ApiResponse<WageCateRespone> create(@RequestBody WageCateRequest request){
        return ApiResponse.<WageCateRespone>builder()
                .result(wageCateService.create(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CATG_VIEW')")
    @GetMapping("/type")
    ApiResponse<List<WageCateRespone>> getAll(@RequestParam(name = "type", required = false) String type){
        return ApiResponse.<List<WageCateRespone>>builder()
                .result(wageCateService.getType(type))
                .build();
    }

    @GetMapping("/wages")
    ApiResponse<WageCateRespone> getWage(@RequestParam int wageCateId){
        return ApiResponse.<WageCateRespone>builder()
                .result(wageCateService.getWageCate(wageCateId))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CATG_VIEW')")
    @GetMapping
    ApiResponse<List<WageCateRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "name", required = false) String name,
                                              @RequestParam(name = "symbol", required = false) String symbol,
                                             @RequestParam(name = "salaryType", required = false)  String salaryType){
        return ApiResponse.<List<WageCateRespone>>builder()
                .result(wageCateService.searchAll(pageNumber, 30, name, symbol, salaryType))
                .page(wageCateService.getPagination(pageNumber, 30, name, symbol, salaryType))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CATG_EDIT')")
    @PutMapping
    ApiResponse<WageCateRespone> update(@RequestParam int wageCateId, @RequestBody WageCateRequest request){
        return ApiResponse.<WageCateRespone>builder()
                .result(wageCateService.update(wageCateId, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CATG_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int dayOffId){
        wageCateService.deleteWageCate(dayOffId);
        return ApiResponse.<String>builder()
                .result("This salary category has been deleted")
                .build();
    }
}
