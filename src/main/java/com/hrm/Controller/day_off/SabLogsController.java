package com.hrm.Controller.day_off;

import com.hrm.Service.day_off.SabLogsService;
import com.hrm.dto.request.dayOff.SabbaticalLogsRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.day_off.SabbaticalLogsResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}sabbatical_leave_logs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SabLogsController {
    SabLogsService sabLogsService;

    @PostMapping
    ApiResponse<SabbaticalLogsResponse> create(@RequestBody SabbaticalLogsRequest request){
        return ApiResponse.<SabbaticalLogsResponse>builder()
                .result(sabLogsService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<SabbaticalLogsResponse>> getAll(@RequestParam int pageNumber){
        return ApiResponse.<List<SabbaticalLogsResponse>>builder()
                .result(sabLogsService.getAllSab(pageNumber, 30))
                .build();
    }

    @GetMapping("/sab")
    ApiResponse<List<SabbaticalLogsResponse>> getByEmpId(@RequestParam int pageNumber, @RequestParam String id){
        return ApiResponse.<List<SabbaticalLogsResponse>>builder()
                .result(sabLogsService.getByEmpId(id, pageNumber, 30))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<SabbaticalLogsResponse>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "name", required = false) String name){
        return ApiResponse.<List<SabbaticalLogsResponse>>builder()
                .result(sabLogsService.searchAllSab(pageNumber, 30, name))
                .build();
    }
}
