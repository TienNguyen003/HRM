package com.hrm.Controller.day_off;

import com.hrm.Service.day_off.LeaveService;
import com.hrm.dto.request.leave.LeaveRequest;
import com.hrm.dto.request.leave.LeaveUpdateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.day_off.LeaveRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("${api.prefix}day_off_letter")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LeaveController {
    LeaveService leaveService;

    @PostMapping
    ApiResponse<LeaveRespone> create(@RequestBody LeaveRequest leaveRequest){
        return ApiResponse.<LeaveRespone>builder()
                .result(leaveService.createLeave(leaveRequest))
                .build();
    }

    @PutMapping
    ApiResponse<LeaveRespone> update(@RequestParam int leaveId, @RequestBody LeaveRequest request){
        return ApiResponse.<LeaveRespone>builder()
                .result(leaveService.updateLeave(leaveId, request))
                .build();
    }

    @PutMapping("/status")
    ApiResponse<LeaveRespone> updateStt(@RequestParam int leaveId, @RequestBody LeaveUpdateRequest request){
        return ApiResponse.<LeaveRespone>builder()
                .result(leaveService.updateStatus(leaveId, request))
                .build();
    }

    @GetMapping("/leave")
    ApiResponse<LeaveRespone> getLeave(@RequestParam int leaveId){
        return ApiResponse.<LeaveRespone>builder()
                .result(leaveService.getLeave(leaveId))
                .build();
    }

    @GetMapping()
    ApiResponse<List<LeaveRespone>> searchAll(@RequestParam("pageNumber") int pageNumber ,
                                        @RequestParam(name = "name", required = false) String name,
                                        @RequestParam(name = "status", required = false)  Integer status,
                                        @RequestParam(name = "dayOff", required = false) Integer dayOff){
        return ApiResponse.<List<LeaveRespone>>builder()
                .result(leaveService.searchAll(pageNumber, 30, name, dayOff, status))
                .page(leaveService.getPagination(pageNumber, 30, name, dayOff, status))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete (@RequestParam int leaveId){
        leaveService.deleteLeave(leaveId);
        return ApiResponse.<String>builder()
                .result("Day off has been deleted")
                .build();
    }
}
