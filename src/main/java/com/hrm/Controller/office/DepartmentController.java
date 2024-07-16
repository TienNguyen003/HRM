package com.hrm.Controller.office;

import com.hrm.Service.office.DepartmentService;
import com.hrm.dto.request.office.DepartmentRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.office.DepartmentResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}structures")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentController {
    DepartmentService departmentService;

    @PostMapping
    ApiResponse<DepartmentResponse> create(@RequestBody DepartmentRequest request){
        return ApiResponse.<DepartmentResponse>builder()
                .result(departmentService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<DepartmentResponse>> getAll(){
        return ApiResponse.<List<DepartmentResponse>>builder()
                .result(departmentService.getAll())
                .build();
    }

    @GetMapping("/department")
    ApiResponse<DepartmentResponse> getD(@RequestParam int departmentId){
        return ApiResponse.<DepartmentResponse>builder()
                .result(departmentService.getById(departmentId))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<DepartmentResponse>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "name", required = false) String name,
                                                 @RequestParam(name = "shortName", required = false) String shortName,
                                             @RequestParam(name = "status", required = false)  Integer status){
        return ApiResponse.<List<DepartmentResponse>>builder()
                .result(departmentService.searchAll(pageNumber, 30, name, shortName,status))
                .build();
    }

    @PutMapping
    ApiResponse<DepartmentResponse> update(@RequestParam int departmentId, @RequestBody DepartmentRequest request){
        return ApiResponse.<DepartmentResponse>builder()
                .result(departmentService.update(departmentId, request))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int departmentId){
        departmentService.delete(departmentId);
        return ApiResponse.<String>builder()
                .result("Department has been deleted")
                .build();
    }
}
