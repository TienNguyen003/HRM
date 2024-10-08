package com.hrm.Controller.day_off;

import com.hrm.Service.day_off.HolidayService;
import com.hrm.dto.request.dayOff.HolidayRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.day_off.HolidayResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}holidays")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HolidayController {
    HolidayService holidayService;

    @PreAuthorize("@requiredPermission.checkPermission('HOLI_ADD')")
    @PostMapping
    ApiResponse<HolidayResponse> create(@RequestBody @Valid HolidayRequest request){
        return ApiResponse.<HolidayResponse>builder()
                .result(holidayService.createH(request))
                .build();
    }

    @GetMapping("/day")
    ApiResponse<HolidayResponse> getH(@RequestParam int holidayId){
        return ApiResponse.<HolidayResponse>builder()
                .result(holidayService.getH(holidayId))
                .build();
    }

//    @PreAuthorize("@requiredPermission.checkPermission('HOLI_VIEW')")
    @GetMapping
    ApiResponse<List<HolidayResponse>> search(@RequestParam("pageNumber") int pageNumber ,
                                              @RequestParam(name = "name", required = false) String name){
        return ApiResponse.<List<HolidayResponse>>builder()
                .result(holidayService.searchAll(pageNumber, 30, name))
                .page(holidayService.getPagination(pageNumber, 30, name))
                .build();
    }

    @GetMapping("/time")
    ApiResponse<Integer> getTime(@RequestParam("time") String time){
        return ApiResponse.<Integer>builder()
                .result(holidayService.getTime(time))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('HOLI_EDIT')")
    @PutMapping
    ApiResponse<HolidayResponse> update(@RequestParam int holidayId,@RequestBody @Valid HolidayRequest request){
        return ApiResponse.<HolidayResponse>builder()
                .result(holidayService.updateH(holidayId, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('HOLI_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int holidayId){
        holidayService.deleteH(holidayId);
        return ApiResponse.<String>builder()
                .result("Day off has been deleted")
                .build();
    }
}
