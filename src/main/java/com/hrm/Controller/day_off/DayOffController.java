package com.hrm.Controller.day_off;

import com.hrm.Service.day_off.DayOffService;
import com.hrm.dto.request.dayOff.dayoff.DayOffRequest;
import com.hrm.dto.request.dayOff.dayoff.DayOffUpdateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.day_off.DayOffResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}day_off_categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DayOffController {
    DayOffService dayOffService;

    @PreAuthorize("@requiredPermission.checkPermission('LEAV_ADD')")
    @PostMapping
    ApiResponse<DayOffResponse> create(@RequestBody @Valid DayOffRequest request){
        return ApiResponse.<DayOffResponse>builder()
                .result(dayOffService.createDayOff(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAV_VIEW')")
    @GetMapping
    ApiResponse<List<DayOffResponse>> getAll(){
        return ApiResponse.<List<DayOffResponse>>builder()
                .result(dayOffService.getAllDayOff())
                .build();
    }

    @GetMapping("/day")
    ApiResponse<DayOffResponse> getDay(@RequestParam int dayOffId){
        return ApiResponse.<DayOffResponse>builder()
                .result(dayOffService.getDayOff(dayOffId))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAV_VIEW')")
    @GetMapping("/search")
    ApiResponse<List<DayOffResponse>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "nameDay", required = false) String nameDay,
                                             @RequestParam(name = "status", required = false)  Integer status){
        return ApiResponse.<List<DayOffResponse>>builder()
                .result(dayOffService.searchAll(pageNumber, 30, nameDay, status))
                .page(dayOffService.getPagination(pageNumber, 30, nameDay, status))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAV_EDIT')")
    @PutMapping
    ApiResponse<DayOffResponse> update(@RequestParam int dayOffId,@RequestBody @Valid DayOffUpdateRequest request){
        return ApiResponse.<DayOffResponse>builder()
                .result(dayOffService.updateDayOff(dayOffId, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAV_EDIT')")
    @PutMapping("/stt")
    ApiResponse<String> updateStt(@RequestParam int id, @RequestParam int status){
        return ApiResponse.<String>builder()
                .result(dayOffService.updateStt(id, status))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('LEAV_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int dayOffId){
        dayOffService.deleteDayOff(dayOffId);
        return ApiResponse.<String>builder()
                .result("Day off has been deleted")
                .build();
    }
}
