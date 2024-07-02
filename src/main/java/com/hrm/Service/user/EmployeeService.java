package com.hrm.Service.user;

import com.hrm.Entity.user.Employee;
import com.hrm.Mapper.user.EmployeeMapper;
import com.hrm.dto.request.user.EmployeeRequest;
import com.hrm.dto.response.user.EmployeeRespone;
import com.hrm.repository.user.EmployeeRepository;
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

    // thêm danh sách
    public EmployeeRespone createB(EmployeeRequest request) {
        Employee employee = employeeMapper.toEmployee(request);

        return employeeMapper.toEmployeeRespone(employeeRepository.save(employee));
    }

    // cập nhật
    public EmployeeRespone updateB(int bankId, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("This bank not found"));

        employeeMapper.updateEmployee(employee, request);

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
                .orElseThrow(() -> new RuntimeException("This employee not found")));
    }

    // tìm kiếm
//    public List<EmployeeRespone> searchAllB
//    (int pageNumber, int pageSize, String name, String nameDay, Integer status, Integer priority) {
//        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
//        return employeeRepository.findBankByNameAndNameBank(name, status, priority, nameDay, pageable)
//                .stream().map(employeeMapper::toBankRespone).toList();
//    }

    // xóa
    public void deleteB(int id) {
        employeeRepository.deleteById(id);
    }

}
