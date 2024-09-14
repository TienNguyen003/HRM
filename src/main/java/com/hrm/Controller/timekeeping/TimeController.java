package com.hrm.Controller.timekeeping;

import com.hrm.Service.timekeeping.TimeService;
import com.hrm.dto.request.timekeeping.TimeKeepingRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.timekeeping.TimeKeepingRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}checks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TimeController {
    TimeService timeService;

    @PreAuthorize("@requiredPermission.checkPermission('ATTD_ADD')")
    @PostMapping
    ApiResponse<TimeKeepingRespone> create(@RequestBody TimeKeepingRequest request){
        return ApiResponse.<TimeKeepingRespone>builder()
                .result(timeService.createTime(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ATTD_VIEW')")
    @GetMapping
    ApiResponse<List<TimeKeepingRespone>> searchAll(@RequestParam int pageNumber,
                                                    @RequestParam(name = "name", required = false) String name,
                                                    @RequestParam(name = "day", required = false) String day,
                                                    @RequestParam(name = "office", required = false) String office,
                                                    @RequestParam(name = "department", required = false) String department){
        return ApiResponse.<List<TimeKeepingRespone>>builder()
                .result(timeService.getTimes(name, day, department, office, pageNumber, 30))
                .page(timeService.getPagination(name, day, department, office, pageNumber, 30))
                .build();
    }

    @GetMapping("/get")
    ApiResponse<List<TimeKeepingRespone>> getAll(){
        return ApiResponse.<List<TimeKeepingRespone>>builder()
                .result(timeService.getAll())
                .build();
    }

    @GetMapping("/time")
    ApiResponse<TimeKeepingRespone> getRole(@RequestParam int id){
        return ApiResponse.<TimeKeepingRespone>builder()
                .result(timeService.getTime(id))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ATTD_EDIT')")
    @PutMapping
    ApiResponse<TimeKeepingRespone> updateTimeKeeping(@RequestBody TimeKeepingRequest request, @RequestParam int id){
        return ApiResponse.<TimeKeepingRespone>builder()
                .result(timeService.updateTime(id, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('ATTD_DELETE')")
    @DeleteMapping
    ApiResponse<String> delete(@RequestParam int id){
        timeService.deleteTime(id);
        return ApiResponse.<String>builder()
                .result("Role has been deleted")
                .build();
    }
}
