package com.hrm.Service.notification;

import com.hrm.Controller.RequiredPermission;
import com.hrm.Entity.notification.Notification;
import com.hrm.Entity.user.Contracts;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.user.User;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.repository.day_off.LeaveRepository;
import com.hrm.repository.job.DisqualifiedRepository;
import com.hrm.repository.notification.NotificationRepository;
import com.hrm.repository.user.ContractsRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.user.UserRepository;
import com.hrm.repository.wage.AdvanceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    UserRepository userRepository;
    EmployeeRepository employeeRepository;
    ContractsRepository contractsRepository;
    LeaveRepository leaveRepository;
    AdvanceRepository advanceRepository;
    DisqualifiedRepository disqualifiedRepository;
    RequiredPermission requiredPermission;
    SimpMessagingTemplate messagingTemplate;

    static final int PAGE_SIZE = 20;

    // id tài khoản đang đăng nhập
    public String currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));
        return user.getId();
    }

    // gửi thông báo cho 1 user + push websocket
    public void push(String userId, String title, String content, String link) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .link(link)
                .status(0)
                .createTime(String.valueOf(LocalDateTime.now()))
                .build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications", userId);
    }

    // gửi thông báo cho nhân viên (tìm user qua employee)
    public void pushToEmployee(Employee employee, String title, String content, String link) {
        userRepository.findByEmployeeId(employee.getId())
                .ifPresent(user -> push(user.getId(), title, content, link));
    }

    public List<Notification> myNotifications() {
        return notificationRepository.findByUserIdOrderByCreateTimeDesc(currentUserId(), PageRequest.of(0, PAGE_SIZE));
    }

    public long unreadCount() {
        return notificationRepository.countByUserIdAndStatus(currentUserId(), 0);
    }

    public void markRead(int id) {
        Notification notification = notificationRepository.findByIdAndUserId(id, currentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        notification.setStatus(1);
        notificationRepository.save(notification);
    }

    public void markAllRead() {
        List<Notification> list = notificationRepository.findByUserIdAndStatus(currentUserId(), 0);
        list.forEach(n -> n.setStatus(1));
        notificationRepository.saveAll(list);
    }

    // ---- nhắc việc tính toán trực tiếp (không lưu) ----
    public List<Map<String, Object>> reminders() {
        List<Map<String, Object>> result = new ArrayList<>();

        if (checkSafe("REQ_APPROVALS")) {
            long pending = leaveRepository.countByStatus(0);
            if (pending > 0) result.add(reminder("Đơn nghỉ phép chờ duyệt",
                    pending + " đơn nghỉ phép đang chờ bạn xử lý", "/day_off_letters/approvals"));
        }
        if (checkSafe("ADV_APPROVALS")) {
            long pending = advanceRepository.countByStatus(0);
            if (pending > 0) result.add(reminder("Ứng lương chờ duyệt",
                    pending + " phiếu ứng lương đang chờ bạn xử lý", "/advances/approvals"));
        }
        if (checkSafe("CV_VIEW")) {
            long pending = disqualifiedRepository.countByStatus(0);
            if (pending > 0) result.add(reminder("CV chờ duyệt",
                    pending + " ứng viên đang chờ xem xét", "/list-qualify"));
        }
        if (checkSafe("CONT_VIEW")) {
            String today = LocalDate.now().toString();
            String plus30 = LocalDate.now().plusDays(30).toString();
            List<Contracts> expiring = contractsRepository.findExpiring(today, plus30);
            if (!expiring.isEmpty()) result.add(reminder("Hợp đồng sắp hết hạn",
                    expiring.size() + " hợp đồng sắp đến ngày kết thúc", "/users/contracts"));
        }

        // sinh nhật hôm nay
        String todaySuffix = String.format("-%02d-%02d", LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        List<String> birthdays = employeeRepository.findAll().stream()
                .filter(e -> e.getBirth_date() != null && e.getBirth_date().endsWith(todaySuffix))
                .map(Employee::getName)
                .toList();
        if (!birthdays.isEmpty()) result.add(reminder("Sinh nhật hôm nay",
                String.join(", ", birthdays), "/users"));

        return result;
    }

    Map<String, Object> reminder(String title, String content, String link) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("link", link);
        map.put("reminder", true);
        return map;
    }

    // check permission an toàn (không throw ra ngoài)
    boolean checkSafe(String permission) {
        try {
            return requiredPermission.checkPermission(permission);
        } catch (Exception e) {
            return false;
        }
    }
}