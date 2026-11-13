package com.hrm.Controller.job;

import com.hrm.Service.job.JobPostingService;
import com.hrm.dto.request.job.JobPostingRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.job.JobPostingResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}job_posting")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobPostingController {
    JobPostingService jobPostingService;

    @PreAuthorize("@requiredPermission.checkPermission('CV_ADD')")
    @PostMapping
    ApiResponse<JobPostingResponse> create(@RequestBody @Valid JobPostingRequest request) {
        return ApiResponse.<JobPostingResponse>builder()
                .result(jobPostingService.create(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_VIEW')")
    @GetMapping
    ApiResponse<List<JobPostingResponse>> search(@RequestParam("pageNumber") int pageNumber,
                                                 @RequestParam(name = "pageSize", defaultValue = "30") int pageSize,
                                                 @RequestParam(name = "title", required = false) String title,
                                                 @RequestParam(name = "status", required = false) Integer status) {
        return ApiResponse.<List<JobPostingResponse>>builder()
                .result(jobPostingService.search(pageNumber, pageSize, title, status))
                .page(jobPostingService.getPagination(pageNumber, pageSize, title, status))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_VIEW')")
    @GetMapping("/detail")
    ApiResponse<JobPostingResponse> getDetail(@RequestParam int id) {
        return ApiResponse.<JobPostingResponse>builder()
                .result(jobPostingService.getById(id))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_EDIT')")
    @PutMapping
    ApiResponse<JobPostingResponse> update(@RequestParam int id,
                                           @RequestBody @Valid JobPostingRequest request) {
        return ApiResponse.<JobPostingResponse>builder()
                .result(jobPostingService.update(id, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete(@RequestParam int id) {
        jobPostingService.delete(id);
        return ApiResponse.<String>builder()
                .result("Tin tuyển dụng đã được xóa")
                .build();
    }
}
