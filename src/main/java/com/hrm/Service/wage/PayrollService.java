package com.hrm.Service.wage;

import com.hrm.Entity.department.Department;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Payroll;
import com.hrm.Mapper.wage.PayRollMapper;
import com.hrm.dto.request.wage.PayrollRequest;
import com.hrm.dto.response.wage.PayrollRespone;
import com.hrm.repository.DepartmentRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.PayrollRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayrollService {
    PayrollRepository payrollRepository;
    PayRollMapper payRollMapper;
    EmployeeRepository employeeRepository;
    DepartmentRepository departmentRepository;

    // thêm danh sách
    public PayrollRespone create(PayrollRequest request){
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("No employee not found"));
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Payroll payroll = payRollMapper.toPayRoll(request);

        payroll.setEmployee(employee);
        payroll.setDepartment(department);

        return payRollMapper.toPayRollRespone(payrollRepository.save(payroll));
    }

    // cập nhật
    public PayrollRespone update(int payrollId, PayrollRequest request){
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("This salary not found"));

        payRollMapper.updatePayRoll(payroll, request);

        return payRollMapper.toPayRollRespone(payrollRepository.save(payroll));
    }

    // lấy ra tất cả
    public List<PayrollRespone> getAllPayroll(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return payrollRepository.findAll(pageable)
                .stream().map(payRollMapper::toPayRollRespone).toList();
    }

    // lấy theo id
    public PayrollRespone getPayroll(int wageIdEmployee){
        return payRollMapper.toPayRollRespone(payrollRepository.findById(wageIdEmployee)
                .orElseThrow(() -> new RuntimeException("This salary  not found")));
    }

    // tìm kiếm
    public List<PayrollRespone> searchAll(int pageNumber, int pageSize, String name, String time){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return payrollRepository.findByNameAndTime(name, time, pageable)
                .stream().map(payRollMapper::toPayRollRespone).toList();
    }

    // xóa
    public void deletPayroll(int payrollId){
        payrollRepository.deleteById(payrollId);
    }

}
