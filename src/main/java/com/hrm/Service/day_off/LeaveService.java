package com.hrm.Service.day_off;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.day_off.DayOffCategories;
import com.hrm.Entity.day_off.SabbaticalLeaveLogs;
import com.hrm.Entity.hrm.HrmLeaveRequest;
import com.hrm.Entity.user.Employee;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Service.hrm.HrmService;
import com.hrm.Service.notification.NotificationService;
import com.hrm.dto.request.leave.LeaveRequest;
import com.hrm.dto.request.leave.LeaveUpdateRequest;
import com.hrm.dto.response.day_off.LeaveRespone;
import com.hrm.repository.day_off.DayOffRepository;
import com.hrm.repository.day_off.SabLogsRepository;
import com.hrm.repository.hrm.HrmLeaveRequestRepository;
import com.hrm.repository.user.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * API cũ /api/day_off_letter giờ dùng chung nguồn duy nhất HrmLeaveRequest.
 * Giữ nguyên contract int id / status số cho frontend cũ.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeaveService {
    HrmService hrmService;
    HrmLeaveRequestRepository leaveRequestRepository;
    EmployeeRepository employeeRepository;
    DayOffRepository dayOffRepository;
    SabLogsRepository sabLogsRepository;
    NotificationService notificationService;

    private String statusText(int status) {
        return switch (status) {
            case 1 -> "Approved";
            case 2 -> "Rejected";
            case 3 -> "Cancelled";
            default -> "Pending";
        };
    }

    private int statusInt(String status) {
        if (status == null) return 0;
        return switch (status) {
            case "Approved" -> 1;
            case "Rejected" -> 2;
            case "Cancelled" -> 3;
            default -> 0;
        };
    }

    private LeaveRespone toResponse(HrmLeaveRequest l) {
        DayOffCategories category = dayOffRepository.findByNameDay(l.getType()).orElse(null);
        return LeaveRespone.builder()
                .id(l.getLegacyId() == null ? 0 : l.getLegacyId())
                .startTime(l.getStart() + " 08:00")
                .endTime(l.getEnd() + " 17:00")
                .totalTime(l.getDays() * 8 + " hours")
                .approved(l.getApprover())
                .reason(l.getReason())
                .status(statusInt(l.getStatus()))
                .creationTime(l.getSubmitted())
                .dayOffCategories(category)
                .employee(l.getEmployee())
                .build();
    }

    private HrmLeaveRequest require(int legacyId) {
        return leaveRequestRepository.findByLegacyId(legacyId)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_NOT_EXISTED));
    }

    @Transactional
    public LeaveRespone createLeave(LeaveRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        DayOffCategories category = dayOffRepository.findById(request.getDayOff())
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_NOT_EXISTED));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("employeeId", employee.getHrmId());
        payload.put("type", category.getNameDay());
        payload.put("start", request.getStartTime().substring(0, 10));
        payload.put("end", request.getEndTime().substring(0, 10));
        int hours = parseHours(request.getTotalTime());
        payload.put("days", Math.max(1, (hours + 7) / 8));
        payload.put("reason", request.getReason());

        String id = String.valueOf(hrmService.createLeaveRequest(payload).get("id"));
        return toResponse(leaveRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_NOT_EXISTED)));
    }

    private int parseHours(String value) {
        try {
            return (int) Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 8;
        }
    }

    @Transactional
    public LeaveRespone updateLeave(int id, LeaveRequest request) {
        HrmLeaveRequest leave = require(id);
        if (!"Pending".equals(leave.getStatus())) throw new AppException(ErrorCode.DAYOFF_NOT_EDIT);
        DayOffCategories category = dayOffRepository.findById(request.getDayOff())
                .orElseThrow(() -> new AppException(ErrorCode.DAYCATE_NOT_EXISTED));

        leave.setType(category.getNameDay());
        leave.setStart(request.getStartTime().substring(0, 10));
        leave.setEnd(request.getEndTime().substring(0, 10));
        leave.setDays(Math.max(1, (parseHours(request.getTotalTime()) + 7) / 8));
        leave.setReason(request.getReason());
        leaveRequestRepository.save(leave);
        return toResponse(leave);
    }

    @Transactional
    public LeaveRespone updateStatus(int leaveId, LeaveUpdateRequest request) {
        HrmLeaveRequest leave = require(leaveId);
        if (!"Pending".equals(leave.getStatus())) throw new AppException(ErrorCode.DAYOFF_NOT_EDIT);
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        hrmService.updateLeaveStatus(leave.getId(), statusText(request.getStatus()), request.getNameApproval());

        int hours = request.getTime();
        SabbaticalLeaveLogs log = SabbaticalLeaveLogs.builder()
                .updateTime(LocalDateTime.now())
                .fluctuatesTime(hours)
                .content(request.getNameApproval() + (request.getStatus() == 1 ? " đã duyệt đơn xin nghỉ"
                        : request.getStatus() == 2 ? " đã từ chối đơn xin nghỉ" : " đã hủy đơn xin nghỉ"))
                .remaining(employee.getVacationHours())
                .employee(employee)
                .build();
        sabLogsRepository.save(log);
        notificationService.pushToEmployee(employee, "Kết quả đơn nghỉ phép",
                request.getNameApproval() + (request.getStatus() == 1 ? " đã duyệt đơn nghỉ phép của bạn"
                        : request.getStatus() == 2 ? " đã từ chối đơn nghỉ phép của bạn"
                        : " đã hủy đơn nghỉ phép của bạn"), "/day_off_letters");

        return toResponse(require(leaveId));
    }

    public LeaveRespone getLeave(int leaveId) {
        return toResponse(require(leaveId));
    }

    private List<HrmLeaveRequest> filter(String name, Integer dayOff, Integer status, Integer employeeId) {
        DayOffCategories category = dayOff == null ? null : dayOffRepository.findById(dayOff).orElse(null);
        return leaveRequestRepository.findAll().stream()
                .filter(l -> name == null || name.isBlank() || l.getEmployeeName().toLowerCase().contains(name.toLowerCase()))
                .filter(l -> category == null || category.getNameDay().equals(l.getType()))
                .filter(l -> status == null || statusInt(l.getStatus()) == status)
                .filter(l -> employeeId == null || (l.getEmployee() != null && l.getEmployee().getId() == employeeId))
                .sorted(Comparator.comparing(HrmLeaveRequest::getSubmitted,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer dayOff, Integer status, Integer employeeId) {
        int total = filter(name, dayOff, status, employeeId).size();
        return PageCustom.builder()
                .totalPages(String.valueOf(Math.max(1, (total + pageSize - 1) / pageSize)))
                .totalItems(String.valueOf(total))
                .totalItemsPerPage(String.valueOf(Math.min(pageSize, Math.max(0, total - (pageNumber - 1) * pageSize))))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    public List<LeaveRespone> searchAll(int pageNumber, int pageSize, String name, Integer dayOff, Integer status, Integer employeeId) {
        List<HrmLeaveRequest> all = filter(name, dayOff, status, employeeId);
        int from = Math.max(0, (pageNumber - 1) * pageSize);
        if (from >= all.size()) return List.of();
        return all.subList(from, Math.min(from + pageSize, all.size())).stream().map(this::toResponse).toList();
    }

    public void deleteLeave(int leaveId) {
        leaveRequestRepository.delete(require(leaveId));
    }

    public List<LeaveRespone> getAll(Integer employeeId) {
        return filter(null, null, null, employeeId).stream().map(this::toResponse).toList();
    }
}
