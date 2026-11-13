package com.hrm.Service.extra;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.extra.ExtraRequest;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Wage;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Service.notification.NotificationService;
import com.hrm.dto.request.extra.ExtraRequestCreate;
import com.hrm.dto.request.extra.ExtraStatusUpdate;
import com.hrm.repository.extra.ExtraRequestRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.WageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExtraRequestService {
    ExtraRequestRepository extraRequestRepository;
    EmployeeRepository employeeRepository;
    WageRepository wageRepository;
    NotificationService notificationService;

    static final int BASIC_SALARY_CATEGORY_ID = 4;      // danh mục lương cơ bản
    static final double STANDARD_HOURS_PER_MONTH = 174; // giờ làm chuẩn/tháng (VN)
    static final double OVERTIME_RATE = 1.5;            // 150% theo luật lao động

    public ExtraRequest create(ExtraRequestCreate request) {
        if (request.getType() == 1 && request.getHours() <= 0)
            throw new AppException(ErrorCode.EXTRA_INVALID);
        if (request.getType() == 2 && (request.getPlace() == null || request.getPlace().isBlank()))
            throw new AppException(ErrorCode.EXTRA_INVALID);
        if (request.getEndDate() != null && !request.getEndDate().isBlank()
                && request.getEndDate().compareTo(request.getDate()) < 0)
            throw new AppException(ErrorCode.EXTRA_INVALID);

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        ExtraRequest extra = ExtraRequest.builder()
                .type(request.getType())
                .date(request.getDate())
                .endDate(request.getEndDate() == null || request.getEndDate().isBlank() ? null : request.getEndDate())
                .hours(request.getHours())
                .place(request.getPlace())
                .reason(request.getReason())
                .employee(employee)
                .status(0)
                .creationTime(String.valueOf(LocalDateTime.now()))
                .build();
        return extraRequestRepository.save(extra);
    }

    public ExtraRequest update(int id, ExtraRequestCreate request) {
        ExtraRequest extra = extraRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXTRA_NOT_EXISTED));
        if (extra.getStatus() != 0) throw new AppException(ErrorCode.EXTRA_NOT_EDIT);

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        extra.setType(request.getType());
        extra.setDate(request.getDate());
        extra.setEndDate(request.getEndDate() == null || request.getEndDate().isBlank() ? null : request.getEndDate());
        extra.setHours(request.getHours());
        extra.setPlace(request.getPlace());
        extra.setReason(request.getReason());
        extra.setEmployee(employee);
        return extraRequestRepository.save(extra);
    }

    public ExtraRequest updateStatus(int id, ExtraStatusUpdate request) {
        ExtraRequest extra = extraRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXTRA_NOT_EXISTED));
        if (extra.getStatus() != 0) throw new AppException(ErrorCode.EXTRA_NOT_EDIT);

        extra.setStatus(request.getStatus());
        extra.setApprovedBy(request.getApprovedBy());
        extraRequestRepository.save(extra);

        String typeName = switch (extra.getType()) {
            case 1 -> "tăng ca";
            case 2 -> "công tác";
            default -> "làm việc từ xa";
        };
        notificationService.pushToEmployee(extra.getEmployee(), "Kết quả đơn " + typeName,
                request.getApprovedBy() + (request.getStatus() == 1 ? " đã duyệt đơn " : " đã từ chối đơn ") + typeName + " của bạn",
                "/extra_requests");
        return extra;
    }

    public ExtraRequest getById(int id) {
        return extraRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXTRA_NOT_EXISTED));
    }

    public List<ExtraRequest> search(int pageNumber, int pageSize, String name, Integer type, Integer status, Integer employeeId) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return extraRequestRepository.findByFilters(name, type, status, employeeId, pageable).getContent();
    }

    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer type, Integer status, Integer employeeId) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ExtraRequest> page = extraRequestRepository.findByFilters(name, type, status, employeeId, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    public void delete(int id) {
        ExtraRequest extra = extraRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXTRA_NOT_EXISTED));
        if (extra.getStatus() != 0) throw new AppException(ErrorCode.EXTRA_NOT_EDIT);
        extraRequestRepository.deleteById(id);
    }

    // tiền tăng ca = giờ × (lương cơ bản / 174) × 1.5
    public Map<String, Object> overtimeInfo(int employeeId, String month) {
        String yearMonth = normalizeMonth(month);
        double hours = extraRequestRepository.sumApprovedOvertime(employeeId, yearMonth);

        double hourly = 0;
        Wage basic = wageRepository.existsByEmployeeCate(employeeId, BASIC_SALARY_CATEGORY_ID);
        if (basic != null) {
            try {
                hourly = Double.parseDouble(basic.getSalary()) / STANDARD_HOURS_PER_MONTH;
            } catch (NumberFormatException ignored) {}
        }
        long amount = Math.round(hours * hourly * OVERTIME_RATE);
        return Map.of("hours", hours, "amount", amount, "month", yearMonth);
    }

    // chuẩn hóa month: yyyy-MM <-> M/yyyy
    static String normalizeMonth(String month) {
        if (month == null || month.isBlank()) return "";
        if (month.contains("-")) {
            String[] parts = month.split("-");
            return parts[0] + "-" + String.format("%02d", Integer.parseInt(parts[1]));
        }
        String[] parts = month.split("/");
        String mm = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        return parts[1] + "-" + mm;
    }
}