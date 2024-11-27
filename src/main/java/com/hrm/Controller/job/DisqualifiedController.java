package com.hrm.Controller.job;

import com.hrm.Entity.job.Disqualified;
import com.hrm.Service.job.DisqualifiedService;
import com.hrm.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}disqualified")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DisqualifiedController {
    DisqualifiedService disqualifiedService;

    @GetMapping
    ApiResponse<List<Disqualified>> getAll(){
        return ApiResponse.<List<Disqualified>>builder()
                .result(disqualifiedService.getAllDisqualified())
                .build();
    }

}
