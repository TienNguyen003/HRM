package com.hrm.Controller.notification;

import com.hrm.Service.notification.NotificationService;
import com.hrm.dto.response.ApiResponse;
import com.hrm.Entity.notification.Notification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    // mọi user đã đăng nhập đều dùng được (dữ liệu riêng của họ)
    @GetMapping
    ApiResponse<List<Notification>> myNotifications() {
        return ApiResponse.<List<Notification>>builder()
                .result(notificationService.myNotifications())
                .build();
    }

    @GetMapping("/unread-count")
    ApiResponse<Long> unreadCount() {
        return ApiResponse.<Long>builder()
                .result(notificationService.unreadCount())
                .build();
    }

    @GetMapping("/reminders")
    ApiResponse<List<Map<String, Object>>> reminders() {
        return ApiResponse.<List<Map<String, Object>>>builder()
                .result(notificationService.reminders())
                .build();
    }

    @PutMapping("/read")
    ApiResponse<String> markRead(@RequestParam int id) {
        notificationService.markRead(id);
        return ApiResponse.<String>builder().result("OK").build();
    }

    @PutMapping("/read-all")
    ApiResponse<String> markAllRead() {
        notificationService.markAllRead();
        return ApiResponse.<String>builder().result("OK").build();
    }
}