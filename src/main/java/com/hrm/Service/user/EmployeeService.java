package com.hrm.Service.user;

import com.hrm.Entity.office.Department;
import com.hrm.Entity.user.Employee;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.user.EmployeeMapper;
import com.hrm.dto.request.user.employee.EmployeeDismissalRequest;
import com.hrm.dto.request.user.employee.EmployeeRequest;
import com.hrm.dto.response.user.EmployeeRespone;
import com.hrm.repository.office.DepartmentRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.user.UserRepository;
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
public class EmployeeService {
    EmployeeMapper employeeMapper;
    EmployeeRepository employeeRepository;
    DepartmentRepository departmentRepository;
    UserRepository userRepository;

    // thêm danh sách
    public EmployeeRespone createB(EmployeeRequest request, String username) {
        if(userRepository.existsByUsername(username))
            throw new AppException(ErrorCode.USERNAME_EXISTED);

        Employee employee = employeeMapper.toEmployee(request);
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_EXISTED));

        employee.setDepartment(department);

        return employeeMapper.toEmployeeRespone(employeeRepository.save(employee));
    }

    // cập nhật
    public EmployeeRespone updateB(int employeeId, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        employeeMapper.updateEmployee(employee, request);

        return employeeMapper.toEmployeeRespone(employeeRepository.save(employee));
    }

    // cập nhật ngay sa thai
    public EmployeeRespone updateDismissal (int employeeId, EmployeeDismissalRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        employeeMapper.updateEmployeeDismissal(employee, request);

        return employeeMapper.toEmployeeRespone(employeeRepository.save(employee));
    }

    // lấy ra tất cả
    public List<EmployeeRespone> getAllB(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return employeeRepository.findAll(pageable)
                .stream().map(employeeMapper::toEmployeeRespone).toList();
    }

    // lấy theo id
    public EmployeeRespone getById(int id) {
        return employeeMapper.toEmployeeRespone(employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED)));
    }

    // xóa
    public void deleteB(int id) {
        employeeRepository.deleteById(id);
    }

}
