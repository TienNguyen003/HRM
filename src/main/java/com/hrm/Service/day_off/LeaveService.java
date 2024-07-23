package com.hrm.Service.day_off;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.day_off.ApplicationLeave;
import com.hrm.Entity.day_off.DayOffCategories;
import com.hrm.Entity.user.Employee;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.day_off.LeaveMapper;
import com.hrm.dto.request.leave.LeaveRequest;
import com.hrm.dto.request.leave.LeaveUpdateRequest;
import com.hrm.dto.response.day_off.LeaveRespone;
import com.hrm.repository.day_off.DayOffRepository;
import com.hrm.repository.day_off.LeaveRepository;
import com.hrm.repository.user.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeaveService {
    LeaveRepository leaveRepository;
    LeaveMapper leaveMapper;
    EmployeeRepository employeeRepository;
    DayOffRepository dayOffRepository;

    // tạo đơn xin nghỉ
    public LeaveRespone createLeave(LeaveRequest leaveRequest){
        DayOffCategories dayOffCategories = dayOffRepository.findById(leaveRequest.getDayOff())
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_NOT_EXISTED));
        Employee employee = employeeRepository.findById(leaveRequest.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        employee.setVacationHours(employee.getVacationHours() - Integer.parseInt(leaveRequest.getTotalTime()));
        employee.setHourOff(employee.getHourOff() + Integer.parseInt(leaveRequest.getTotalTime()));

        ApplicationLeave applicationLeave = leaveMapper.toLeave(leaveRequest);
        applicationLeave.setEmployee(employee);
        applicationLeave.setDayOffCategories(dayOffCategories);

        return leaveMapper.toLeaveResponse(leaveRepository.save(applicationLeave));
    }

    // cập nhật đơn nghỉ
    public LeaveRespone updateLeave(int id, LeaveUpdateRequest leaveUpdateRequest){
        ApplicationLeave applicationLeave = leaveRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_EXISTED));
        Employee employee = employeeRepository.findById(leaveUpdateRequest.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        employee.setVacationTime(employee.getVacationTime() - Integer.parseInt(leaveUpdateRequest.getTotalTime()));
        employeeRepository.save(employee);
        leaveMapper.updateLeave(applicationLeave, leaveUpdateRequest);

        return leaveMapper.toLeaveResponse(leaveRepository.save(applicationLeave));
    }

    // lấy theo id đơn
    public LeaveRespone getLeave(int leaveId){
        return leaveMapper.toLeaveResponse(leaveRepository.findById(leaveId)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_EXISTED)));
    }

    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer dayOff, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ApplicationLeave> page = leaveRepository.findByNameContainingAndDayOffAndStatus(name, dayOff, status, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // tìm kiếm đơn
    public List<LeaveRespone> searchAll(int pageNumber, int pageSize, String name, Integer dayOff, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return leaveRepository.findByNameContainingAndDayOffAndStatus(name, dayOff, status, pageable)
                .stream().map(leaveMapper::toLeaveResponse).toList();
    }

    //xóa đơn
    public void deleteLeave(int leaveId){
        leaveRepository.deleteById(leaveId);
    }
}
