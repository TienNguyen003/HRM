package com.hrm.Controller.day_off;

import com.hrm.Service.day_off.SabLogsService;
import com.hrm.dto.request.dayOff.SabbaticalLogsRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.day_off.SabbaticalLogsResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}sabbatical_leave_logs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SabLogsController {
    SabLogsService sabLogsService;

    @PreAuthorize("@requiredPermission.checkPermission('REQ_APPROVALS')")
    @PostMapping
    ApiResponse<SabbaticalLogsResponse> create(@RequestBody @Valid SabbaticalLogsRequest request){
        return ApiResponse.<SabbaticalLogsResponse>builder()
                .result(sabLogsService.create(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('HIST_VIEW')")
    @GetMapping()
    ApiResponse<List<SabbaticalLogsResponse>> search(@RequestParam("pageNumber") int pageNumber ,
                                                     @RequestParam(name = "name", required = false) String name,
                                                     @RequestParam(name = "id", required = false) Integer id){
        return ApiResponse.<List<SabbaticalLogsResponse>>builder()
                .result(sabLogsService.searchAllSab(pageNumber, 30, name, id))
                .page(sabLogsService.getPagination(pageNumber, 30, name, id))
                .build();
    }
}
