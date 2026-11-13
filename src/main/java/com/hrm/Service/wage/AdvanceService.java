package com.hrm.Service.wage;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Advance;
import com.hrm.Entity.wage.Wage;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.wage.AdvanceMapper;
import com.hrm.dto.request.wage.advance.AdvanceRequest;
import com.hrm.dto.request.wage.advance.AdvanceUpdateSttRequest;
import com.hrm.dto.response.wage.AdvanceRespone;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.AdvanceRepository;
import com.hrm.repository.wage.WageRepository;
import com.hrm.Service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdvanceService {
    AdvanceRepository advanceRepository;
    EmployeeRepository employeeRepository;
    WageRepository wageRepository;
    AdvanceMapper advanceMapper;
    NotificationService notificationService;

    // 1 tháng tối đa 3 lần ứng lương
    private static final int ADVANCE_MAX_PER_MONTH = 3;
    // số tiền ứng tối đa = 3 tháng lương cơ bản
    private static final int ADVANCE_LIMIT_MONTHS_OF_SALARY = 3;
    // danh mục lương cơ bản
    private static final int BASIC_SALARY_CATEGORY_ID = 4;

    // thêm danh sách
    public AdvanceRespone create(AdvanceRequest request){
        Advance advance = advanceMapper.toAdvance(request);

        int checkAdvance = advanceRepository.countAdvanceMonth(request.getEmployeeId(), request.getRequestTime());
        if(checkAdvance >= ADVANCE_MAX_PER_MONTH)
            throw new AppException(ErrorCode.ADVANCE_NOT_TOO_TIME, ADVANCE_MAX_PER_MONTH);

        advance.setEmployee(employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.ADVANCE_NOT_EXISTED)));

        Wage wage = wageRepository.existsByEmployeeCate(request.getEmployeeId(), BASIC_SALARY_CATEGORY_ID);
        if(wage == null) throw new AppException(ErrorCode.WAGE_NOT_EXISTED);

        long basicSalary = Long.parseLong(wage.getSalary());
        long maxMoney = basicSalary * ADVANCE_LIMIT_MONTHS_OF_SALARY;
        if(Long.parseLong(request.getMoney()) > maxMoney)
            throw new AppException(ErrorCode.ADVANCE_NOT_TOO_MONTH, basicSalary, ADVANCE_LIMIT_MONTHS_OF_SALARY);

        return advanceMapper.toAdvanceRespone(advanceRepository.save(advance));
    }

    // cập nhật
    public AdvanceRespone update(int advanceId, AdvanceRequest request){
        Advance advance = advanceRepository.findById(advanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ADVANCE_NOT_EXISTED));
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        if(advance.getStatus() != 0) throw new AppException(ErrorCode.ADVANCE_NOT_EDIT);
        else {
            advance.setEmployee(employee);
            advanceMapper.updateAdvance(advance, request);
            advanceRepository.save(advance);
        }

        return advanceMapper.toAdvanceRespone(advance);
    }
    // cập nhật status
    public AdvanceRespone updateStt(int advanceId, AdvanceUpdateSttRequest request){
        Advance advance = advanceRepository.findById(advanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ADVANCE_NOT_EXISTED));

        if(advance.getStatus() != 0) throw new AppException(ErrorCode.ADVANCE_NOT_EDIT);
        else {
            advanceMapper.updateAdvanceStt(advance, request);
            advanceRepository.save(advance);

            // thông báo kết quả duyệt cho nhân viên
            notificationService.pushToEmployee(advance.getEmployee(), "Kết quả ứng lương",
                    request.getApprovedBy() + (request.getStatus() == 1 ? " đã duyệt phiếu ứng lương của bạn"
                            : request.getStatus() == 2 ? " đã từ chối phiếu ứng lương của bạn"
                            : " đã hủy phiếu ứng lương của bạn"),
                    "/advances");
        }

        return advanceMapper.toAdvanceRespone(advance);
    }

    // lấy ra tất cả
    public List<AdvanceRespone> getAll(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return advanceRepository.findAll(pageable)
                .stream().map(advanceMapper::toAdvanceRespone).toList();
    }

    public Integer getMoney(int id, int status, String requestTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/yyyy");
        LocalDateTime startRequestTime = null;
        LocalDateTime endRequestTime = null;

        try {
            // Chuyển đổi chuỗi thành YearMonth
            YearMonth yearMonth = YearMonth.parse(requestTime, formatter);

            // Lấy ngày đầu tháng
            LocalDate firstDayOfMonth = yearMonth.atDay(1);
            startRequestTime = firstDayOfMonth.atStartOfDay();

            // Lấy ngày cuối tháng
            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
            endRequestTime = lastDayOfMonth.atStartOfDay();
        } catch (DateTimeParseException e) {
            System.out.println("Ngày tháng không hợp lệ. Vui lòng nhập theo định dạng M/yyyy.");
        }
        return advanceRepository.money(id, status, startRequestTime, endRequestTime);
    }

    // lấy theo id
    public AdvanceRespone getAdvance(int advanceId){
        Advance advance = advanceRepository.findById(advanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ADVANCE_NOT_EXISTED));

        return advanceMapper.toAdvanceRespone(advance);
    }

    // tìm kiếm
    public List<AdvanceRespone> search(int pageNumber, int pageSize, String name, Integer status, Integer id){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return advanceRepository.findByNameAndStatus(name, status, id, pageable)
                .stream().map(advanceMapper::toAdvanceRespone).toList();
    }
    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer status, Integer id){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Advance> page = advanceRepository.findByNameAndStatus(name, status, id, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa
    public void delete(int advanceId){
        advanceRepository.deleteById(advanceId);
    }
    // lấy thông tin hạn mức ứng lương
    public Map<String, Object> getQuotaInfo(int employeeId){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        Wage wage = wageRepository.existsByEmployeeCate(employeeId, BASIC_SALARY_CATEGORY_ID);
        if(wage == null) throw new AppException(ErrorCode.WAGE_NOT_EXISTED);
        long basicSalary = Long.parseLong(wage.getSalary());
        long maxMoney = basicSalary * ADVANCE_LIMIT_MONTHS_OF_SALARY;
        YearMonth currentMonth = YearMonth.now();
        int countThisMonth = advanceRepository.countAdvanceMonth(employeeId, currentMonth.atDay(1).atStartOfDay());
        long totalApprovedThisMonth = advanceRepository.totalApprovedMoney(employeeId, currentMonth.atDay(1).atStartOfDay(), currentMonth.atEndOfMonth().atStartOfDay());
        return Map.of(
                "basicSalary", basicSalary,
                "maxMoney", maxMoney,
                "maxPerMonth", ADVANCE_MAX_PER_MONTH,
                "countThisMonth", countThisMonth,
                "remainingCount", Math.max(0, ADVANCE_MAX_PER_MONTH - countThisMonth),
                "totalApprovedThisMonth", totalApprovedThisMonth,
                "remainingMoney", Math.max(0, maxMoney - totalApprovedThisMonth)
        );
    }

}
