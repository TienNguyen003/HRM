package com.hrm.Controller.job;

import com.hrm.Entity.job.Disqualified;
import com.hrm.Service.job.DisqualifiedService;
import com.hrm.dto.request.job.DisqualifiedUpdateRequest;
import com.hrm.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}disqualified")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DisqualifiedController {
    DisqualifiedService disqualifiedService;

    @PreAuthorize("@requiredPermission.checkPermission('CV_VIEW')")
    @GetMapping
    ApiResponse<List<Disqualified>> getAll(){
        return ApiResponse.<List<Disqualified>>builder()
                .result(disqualifiedService.getAllDisqualified())
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_VIEW')")
    @GetMapping("/detail")
    ApiResponse<Disqualified> getDetail(@RequestParam int id){
        return ApiResponse.<Disqualified>builder()
                .result(disqualifiedService.getDisqualifiedById(id))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_EDIT')")
    @PutMapping
    ApiResponse<Disqualified> updateStatus(@RequestParam int id,
                                            @RequestBody @Valid DisqualifiedUpdateRequest request){
        return ApiResponse.<Disqualified>builder()
                .result(disqualifiedService.updateStatus(id, request.getStatus()))
                .build();
    }
}
