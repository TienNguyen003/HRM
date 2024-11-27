package com.hrm.Controller.job;

import com.hrm.Service.job.CheckCVService;
import com.hrm.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    @PostMapping("/upload")
    public ApiResponse<List<Map<String, Object>>> uploadCv(@RequestParam("file") MultipartFile[] file,
                                                           @RequestPart("data") List<Map<String, String>> data) {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .result(checkCVService.uploadCV(file, data))
                .build();
    }
}
