package com.hrm.Controller.job;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.job.Disqualified;
import com.hrm.Service.job.CheckCVService;
import com.hrm.Service.job.DisqualifiedService;
import com.hrm.dto.response.ApiResponse;
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

    @PreAuthorize("@requiredPermission.checkPermission('CV_ADD')")
    @PostMapping("/filter")
    public ApiResponse<List<Map<String, Object>>> uploadCv(@RequestParam("file") MultipartFile[] file,
                                                           @RequestPart("data") List<Map<String, String>> data) {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .result(checkCVService.uploadCV(file, data))
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
