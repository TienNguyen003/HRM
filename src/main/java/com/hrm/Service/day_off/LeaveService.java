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

        ApplicationLeave applicationLeave = leaveMapper.toLeave(leaveRequest);
        applicationLeave.setEmployee(employee);
        applicationLeave.setDayOffCategories(dayOffCategories);

        return leaveMapper.toLeaveResponse(leaveRepository.save(applicationLeave));
    }

    // cập nhật đơn nghỉ
    public LeaveRespone updateLeave(int id, LeaveRequest request){
        ApplicationLeave applicationLeave = leaveRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_NOT_EXISTED));
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        DayOffCategories dayOffCategories = dayOffRepository.findById(request.getDayOff())
                .orElseThrow(() -> new AppException(ErrorCode.DAYCATE_NOT_EXISTED));

        if(applicationLeave.getStatus() != 0) throw new AppException(ErrorCode.DAYOFF_NOT_EDIT);
        else {
            leaveMapper.updateLeaveRq(applicationLeave, request);
            applicationLeave.setDayOffCategories(dayOffCategories);
            applicationLeave.setEmployee(employee);
            leaveRepository.save(applicationLeave);
        }

        return leaveMapper.toLeaveResponse(applicationLeave);
    }

    // cap nhat trang thai don
    public LeaveRespone updateStatus(int leaveId, LeaveUpdateRequest request){
        ApplicationLeave applicationLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_NOT_EXISTED));
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        if(applicationLeave.getStatus() != 0) throw new AppException(ErrorCode.DAYOFF_NOT_EDIT);
        else {
            applicationLeave.setStatus(request.getStatus());
            applicationLeave.setApproved(request.getNameApproval());

            if(request.getStatus() == 1){
                employee.setVacationHours(employee.getVacationHours() - request.getTime());
                employee.setHourOff(employee.getHourOff() + request.getTime());
            }
            employeeRepository.save(employee);
            leaveRepository.save(applicationLeave);
        }


        return leaveMapper.toLeaveResponse(applicationLeave);
    }

    // lấy theo id đơn
    public LeaveRespone getLeave(int leaveId){
        return leaveMapper.toLeaveResponse(leaveRepository.findById(leaveId)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_NOT_EXISTED)));
    }

    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer dayOff, Integer status, Integer emplyeeID){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ApplicationLeave> page = leaveRepository.findByNameContainingAndDayOffAndStatus(name, dayOff, status, pageable, emplyeeID);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // tìm kiếm đơn
    public List<LeaveRespone> searchAll(int pageNumber, int pageSize, String name, Integer dayOff, Integer status, Integer emplyeeID){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return leaveRepository.findByNameContainingAndDayOffAndStatus(name, dayOff, status, pageable, emplyeeID)
                .stream().map(leaveMapper::toLeaveResponse).toList();
    }

    //xóa đơn
    public void deleteLeave(int leaveId){
        leaveRepository.deleteById(leaveId);
    }
}
