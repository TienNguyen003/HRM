package com.hrm.Controller.wage;

import com.hrm.Service.wage.AdvanceService;
import com.hrm.dto.request.wage.AdvanceRequest;
import com.hrm.dto.request.wage.AdvanceUpdateSttRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.wage.AdvanceRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}advances")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdvanceController {
    AdvanceService advanceService;

    @PostMapping
    ApiResponse<AdvanceRespone> create(@RequestBody AdvanceRequest request){
        return ApiResponse.<AdvanceRespone>builder()
                .result(advanceService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<AdvanceRespone>> getAll(@RequestParam int pageNumber){
        return ApiResponse.<List<AdvanceRespone>>builder()
                .result(advanceService.getAll(pageNumber, 30))
                .build();
    }

    @GetMapping("/advance")
    ApiResponse<AdvanceRespone> getAdvance(@RequestParam int advaceId){
        return ApiResponse.<AdvanceRespone>builder()
                .result(advanceService.getAdvance(advaceId))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<AdvanceRespone>> search(@RequestParam("pageNumber") int pageNumber ,
                                              @RequestParam(name = "name", required = false) String name,
                                                 @RequestParam(name = "status", required = false) String status){
        return ApiResponse.<List<AdvanceRespone>>builder()
                .result(advanceService.search(pageNumber, 30, name, status))
                .build();
    }

    @PutMapping
    ApiResponse<AdvanceRespone> update(@RequestParam int advaceId, @RequestBody AdvanceRequest request){
        return ApiResponse.<AdvanceRespone>builder()
                .result(advanceService.update(advaceId, request))
                .build();
    }

    @PutMapping("/edit")
    ApiResponse<AdvanceRespone> updateStt(@RequestParam int advaceId, @RequestBody AdvanceUpdateSttRequest request){
        return ApiResponse.<AdvanceRespone>builder()
                .result(advanceService.updateStt(advaceId, request))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int advaceId){
        advanceService.delete(advaceId);
        return ApiResponse.<String>builder()
                .result("This advace has been deleted")
                .build();
    }
}
