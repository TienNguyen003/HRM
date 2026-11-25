package com.hrm.Controller.job;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.job.Disqualified;
import com.hrm.Service.job.CheckCVService;
import com.hrm.Service.job.CvAssessmentService;
import com.hrm.Service.job.DisqualifiedService;
import com.hrm.dto.request.job.CvScoreRequest;
import com.hrm.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}cv")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckCVController {
    CheckCVService checkCVService;
    DisqualifiedService disqualifiedService;
    CvAssessmentService cvAssessmentService;

    @PreAuthorize("@requiredPermission.checkPermission('CV_ADD')")
    @PostMapping("/filter")
    public ApiResponse<List<Map<String, Object>>> uploadCv(@RequestParam("file") MultipartFile[] file,
                                                           @RequestPart(required = false) List<Map<String, String>> data,
                                                           @RequestParam(required = false) Integer assessmentId) {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .result(checkCVService.uploadCV(file, data, assessmentId))
                .build();
    }

    /** Chấm điểm ứng viên theo tiêu chí lưu trong DB. */
    @PreAuthorize("@requiredPermission.checkPermission('CV_EDIT')")
    @PostMapping("/assess/{candidateId}")
    public ApiResponse<Map<String, Object>> scoreCandidate(@PathVariable int candidateId,
                                                           @Valid @RequestBody CvScoreRequest request) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(cvAssessmentService.scoreCandidate(candidateId, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('CV_VIEW')")
    @GetMapping("/assess/{candidateId}")
    public ApiResponse<Map<String, Object>> getScores(@PathVariable int candidateId) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(cvAssessmentService.getScores(candidateId))
                .build();
    }

        @PreAuthorize("@requiredPermission.checkPermission('CV_VIEW')")
        @GetMapping("/qualify")
    public ApiResponse<List<Disqualified>> getQualifyList(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "30") int pageSize,
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "") String phone,
            @RequestParam(required = false) Integer status) {
        List<Disqualified> resultList = disqualifiedService.getDisqualifiedByPage(pageNumber, pageSize, email, phone, status);
        PageCustom pageCustom = disqualifiedService.getPagination(pageNumber, pageSize, email, phone, status);
        return ApiResponse.<List<Disqualified>>builder()
                .result(resultList)
                .page(pageCustom)
                .build();
    }
}
