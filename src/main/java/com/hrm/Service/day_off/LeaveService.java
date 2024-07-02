package com.hrm.Service.day_off;

import com.hrm.Entity.day_off.ApplicationLeave;
import com.hrm.Entity.user.Employee;
import com.hrm.Mapper.day_off.LeaveMapper;
import com.hrm.dto.request.leave.LeaveRequest;
import com.hrm.dto.request.leave.LeaveUpdateRequest;
import com.hrm.dto.response.day_off.LeaveRespone;
import com.hrm.repository.day_off.LeaveRepository;
import com.hrm.repository.user.EmployeeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeaveService {
    LeaveRepository leaveRepository;
    LeaveMapper leaveMapper;
    private final EmployeeRepository employeeRepository;

    // tạo đơn xin nghỉ
    public LeaveRespone createLeave(LeaveRequest leaveRequest){
        ApplicationLeave applicationLeave = leaveMapper.toLeave(leaveRequest);

        return leaveMapper.toLeaveResponse(leaveRepository.save(applicationLeave));
    }

    // cập nhật đơn nghỉ
    public LeaveRespone updateLeave(int id, LeaveUpdateRequest leaveUpdateRequest){
        ApplicationLeave applicationLeave = leaveRepository.findById(id).orElseThrow(() -> new RuntimeException("Day off not found"));
        Employee employee = employeeRepository.findById(leaveUpdateRequest.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("No employee not found"));

        employee.setVacationTime(employee.getVacationTime() - Integer.parseInt(leaveUpdateRequest.getTotalTime()));
        employeeRepository.save(employee);
        leaveMapper.updateLeave(applicationLeave, leaveUpdateRequest);

        return leaveMapper.toLeaveResponse(leaveRepository.save(applicationLeave));
    }

    // lấy tất cả đơn xin nghỉ
    @PreAuthorize("hasRole('ADMIN')")
    public List<LeaveRespone> getAll(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return leaveRepository.findAll(pageable).stream().map(leaveMapper::toLeaveResponse).toList();
    }

    // lấy theo id đơn
    public LeaveRespone getLeave(int leaveId){
        return leaveMapper.toLeaveResponse(leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Day off not found")));
    }

    // tìm kiếm đơn
    public List<LeaveRespone> searchAll(int pageNumber, int pageSize, String name, String dayOff, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return leaveRepository.findByNameContainingAndDayOffAndStatus(name, dayOff, status, pageable)
                .stream().map(leaveMapper::toLeaveResponse).toList();
    }

    //xóa đơn
    public void deleteLeave(int leaveId){
        leaveRepository.deleteById(leaveId);
    }
}
