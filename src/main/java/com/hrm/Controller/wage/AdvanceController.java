package com.hrm.Controller.wage;

import com.hrm.Service.wage.AdvanceService;
import com.hrm.dto.request.wage.advance.AdvanceRequest;
import com.hrm.dto.request.wage.advance.AdvanceUpdateSttRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.wage.AdvanceRespone;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}advances")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdvanceController {
    AdvanceService advanceService;

    @PreAuthorize("@requiredPermission.checkPermission('ADV_ADD')")
    @PostMapping
    ApiResponse<AdvanceRespone> create(@RequestBody @Valid AdvanceRequest request){
        return ApiResponse.<AdvanceRespone>builder()
                .result(advanceService.create(request))
                .build();
    }

    @GetMapping("/advance")
    ApiResponse<AdvanceRespone> getAdvance(@RequestParam int advaceId){
        return ApiResponse.<AdvanceRespone>builder()
                .result(advanceService.getAdvance(advaceId))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ADV_VIEW')")
    @GetMapping
    ApiResponse<List<AdvanceRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "name", required = false) String name,
                                             @RequestParam(name = "status", required = false) Integer status,
                                             @RequestParam(name = "id", required = false) Integer id){
        return ApiResponse.<List<AdvanceRespone>>builder()
                .result(advanceService.search(pageNumber, 30, name, status, id))
                .page(advanceService.getPagination(pageNumber, 30, name, status, id))
                .build();
    }

    @GetMapping("/money")
    ApiResponse<Integer> getMoney(@RequestParam(name = "id", required = false) Integer id,
                                  @RequestParam(name = "time", required = false) String time,
                                  @RequestParam(name = "status", required = false) Integer status){
        return ApiResponse.<Integer>builder()
                .result(advanceService.getMoney(id, status, time))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ADV_EDIT')")
    @PutMapping
    ApiResponse<AdvanceRespone> update(@RequestParam int advaceId,@RequestBody @Valid AdvanceRequest request){
        return ApiResponse.<AdvanceRespone>builder()
                .result(advanceService.update(advaceId, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ADV_APPROVALS')")
    @PutMapping("/approvals")
    ApiResponse<AdvanceRespone> updateStt(@RequestParam int advaceId,@RequestBody @Valid AdvanceUpdateSttRequest request){
        return ApiResponse.<AdvanceRespone>builder()
                .result(advanceService.updateStt(advaceId, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ADV_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int advaceId){
        advanceService.delete(advaceId);
        return ApiResponse.<String>builder()
                .result("This advace has been deleted")
                .build();
    }
}
