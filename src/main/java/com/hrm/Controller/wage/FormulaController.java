package com.hrm.Controller.wage;

import com.hrm.Service.wage.FormulaService;
import com.hrm.dto.request.wage.FormulaRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.wage.FormulaResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}salary_formulas")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FormulaController {
    FormulaService formulaService;

    @PreAuthorize("@requiredPermission.checkPermission('CALC_ADD')")
    @PostMapping
    ApiResponse<FormulaResponse> create(@RequestBody FormulaRequest request){
        return ApiResponse.<FormulaResponse>builder()
                .result(formulaService.create(request))
                .build();
    }

    @GetMapping("/formula")
    ApiResponse<FormulaResponse> getFormula(@RequestParam int id){
        return ApiResponse.<FormulaResponse>builder()
                .result(formulaService.getFormula(id))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CALC_VIEW')")
    @GetMapping("/get-all")
    ApiResponse<List<FormulaResponse>> getAll(){
        return ApiResponse.<List<FormulaResponse>>builder()
                .result(formulaService.getAll())
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CALC_VIEW')")
    @GetMapping
    ApiResponse<List<FormulaResponse>> search(@RequestParam("pageNumber") int pageNumber ,
                                              @RequestParam(name = "name", required = false) String name,
                                                 @RequestParam(name = "status", required = false) Integer status){
        return ApiResponse.<List<FormulaResponse>>builder()
                .result(formulaService.search(pageNumber, 30, name, status))
                .page(formulaService.getPagination(pageNumber, 30, name, status))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CALC_EDIT')")
    @PutMapping
    ApiResponse<FormulaResponse> update(@RequestParam int id, @RequestBody FormulaRequest request){
        return ApiResponse.<FormulaResponse>builder()
                .result(formulaService.update(id, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CALC_EDIT')")
    @PutMapping("/stt")
    ApiResponse<String> updateStt(@RequestParam int id, @RequestParam int status){
        return ApiResponse.<String>builder()
                .result(formulaService.updateStt(id, status))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CALC_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int id){
        formulaService.delete(id);
        return ApiResponse.<String>builder()
                .result("This formula has been deleted")
                .build();
    }
}
