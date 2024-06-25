package com.hrm.Controller.day_off;

import com.hrm.Service.day_off.DayOffService;
import com.hrm.dto.request.dayOff.DayOffRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.day_off.DayOffResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}day_off_categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DayOffController {
    DayOffService dayOffService;

    @PostMapping
    ApiResponse<DayOffResponse> create(@RequestBody DayOffRequest request){
        return ApiResponse.<DayOffResponse>builder()
                .result(dayOffService.createDayOff(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<DayOffResponse>> getAll(@RequestParam int pageNumber){
        return ApiResponse.<List<DayOffResponse>>builder()
                .page(String.valueOf(pageNumber))
                .result(dayOffService.getAllDayOff(pageNumber, 30))
                .build();
    }

    @GetMapping("/day")
    ApiResponse<DayOffResponse> getDay(@RequestParam int dayOffId){
        return ApiResponse.<DayOffResponse>builder()
                .result(dayOffService.getDayOff(dayOffId))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<DayOffResponse>> search(@RequestParam("pageNumber") int pageNumber ,
                                             @RequestParam(name = "nameDay", required = false) String nameDay,
                                             @RequestParam(name = "status", required = false)  Integer status){
        return ApiResponse.<List<DayOffResponse>>builder()
                .result(dayOffService.searchAll(pageNumber, 30, nameDay, status))
                .build();
    }

    @PutMapping
    ApiResponse<DayOffResponse> update(@RequestParam int dayOffId, @RequestBody DayOffRequest request){
        return ApiResponse.<DayOffResponse>builder()
                .result(dayOffService.updateDayOff(dayOffId, request))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int dayOffId){
        dayOffService.deleteDayOff(dayOffId);
        return ApiResponse.<String>builder()
                .result("Day off has been deleted")
                .build();
    }
}
