package com.hrm.Controller.dashboard;

import com.hrm.Service.dashboard.DashboardService;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.dashboard.DashboardResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {
    DashboardService dashboardService;

    // mọi user đã đăng nhập đều xem được số liệu tổng quan
    @GetMapping("/stats")
    ApiResponse<DashboardResponse> getStats() {
        return ApiResponse.<DashboardResponse>builder()
                .result(dashboardService.getStats())
                .build();
    }
}