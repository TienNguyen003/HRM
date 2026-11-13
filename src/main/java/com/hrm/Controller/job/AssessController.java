package com.hrm.Controller.job;

import com.hrm.Service.job.AssessmentService;
import com.hrm.dto.request.job.AssessmentRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.job.AssessmentResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}assessment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssessController {
    AssessmentService assessService;

    @PreAuthorize("@requiredPermission.checkPermission('CV_ADD')")
    @PostMapping
    ApiResponse<AssessmentResponse> create(@RequestBody @Valid AssessmentRequest request){
        return ApiResponse.<AssessmentResponse>builder()
                .result(assessService.createAssess(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_VIEW')")
    @GetMapping
    ApiResponse<List<AssessmentResponse>> getAll(){
        return ApiResponse.<List<AssessmentResponse>>builder()
                .result(assessService.getAllAssess())
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_VIEW')")
    @GetMapping("/assess")
    ApiResponse<AssessmentResponse> getDay(@RequestParam int assessId){
        return ApiResponse.<AssessmentResponse>builder()
                .result(assessService.getAssess(assessId))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_EDIT')")
    @PutMapping
    ApiResponse<AssessmentResponse> update(@RequestParam int assessId,@RequestBody @Valid AssessmentRequest request){
        return ApiResponse.<AssessmentResponse>builder()
                .result(assessService.updateAssess(assessId, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int assessId){
        assessService.deleteAssess(assessId);
        return ApiResponse.<String>builder()
                .result("Assessment has been deleted")
                .build();
    }
}
