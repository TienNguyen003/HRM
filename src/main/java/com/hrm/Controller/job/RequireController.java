package com.hrm.Controller.job;

import com.hrm.Service.job.RequireService;
import com.hrm.dto.request.job.RequirementsRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.job.RequirementsResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("${api.prefix}require")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequireController {
    RequireService requireService;

    @PostMapping
    ApiResponse<RequirementsResponse> create(@RequestBody @Valid RequirementsRequest request){
        return ApiResponse.<RequirementsResponse>builder()
                .result(requireService.createRequire(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RequirementsResponse>> getAll(){
        return ApiResponse.<List<RequirementsResponse>>builder()
                .result(requireService.getAllRequire())
                .build();
    }

    @GetMapping("/require")
    ApiResponse<RequirementsResponse> getDay(@RequestParam int requireId){
        return ApiResponse.<RequirementsResponse>builder()
                .result(requireService.getRequire(requireId))
                .build();
    }

    @GetMapping("/assess")
    ApiResponse<List<RequirementsResponse>> getByAssessId(@RequestParam int assessId){
        return ApiResponse.<List<RequirementsResponse>>builder()
                .result(requireService.getByAssessId(assessId))
                .build();
    }

    @PutMapping
    ApiResponse<RequirementsResponse> update(@RequestParam int requireId,@RequestBody @Valid RequirementsRequest request){
        return ApiResponse.<RequirementsResponse>builder()
                .result(requireService.updateRequire(requireId, request))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int requireId){
        requireService.deleteRequire(requireId);
        return ApiResponse.<String>builder()
                .result("Requirements has been deleted")
                .build();
    }
}
