package com.hrm.Service.wage;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Advance;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.wage.AdvanceMapper;
import com.hrm.dto.request.wage.advance.AdvanceRequest;
import com.hrm.dto.request.wage.advance.AdvanceUpdateSttRequest;
import com.hrm.dto.response.wage.AdvanceRespone;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.AdvanceRepository;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdvanceService {
    AdvanceRepository advanceRepository;
    EmployeeRepository employeeRepository;
    AdvanceMapper advanceMapper;

    // thêm danh sách
    public AdvanceRespone create(AdvanceRequest request){
        Advance advance = advanceMapper.toAdvance(request);

        advance.setEmployee(employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.ADVANCE_NOT_EXISTED)));

        return advanceMapper.toAdvanceRespone(advanceRepository.save(advance));
    }

    // cập nhật
    public AdvanceRespone update(int advanceId, AdvanceRequest request){
        Advance advance = advanceRepository.findById(advanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ADVANCE_NOT_EXISTED));
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        advance.setEmployee(employee);
        advanceMapper.updateAdvance(advance, request);

        return advanceMapper.toAdvanceRespone(advanceRepository.save(advance));
    }
    // cập nhật status
    public AdvanceRespone updateStt(int advanceId, AdvanceUpdateSttRequest request){
        Advance advance = advanceRepository.findById(advanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ADVANCE_NOT_EXISTED));
        advanceMapper.updateAdvanceStt(advance, request);
        return advanceMapper.toAdvanceRespone(advanceRepository.save(advance));
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
    public List<AdvanceRespone> search(int pageNumber, int pageSize, String name, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return advanceRepository.findByNameAndStatus(name, status, pageable)
                .stream().map(advanceMapper::toAdvanceRespone).toList();
    }
    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Advance> page = advanceRepository.findByNameAndStatus(name, status, pageable);
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

}
