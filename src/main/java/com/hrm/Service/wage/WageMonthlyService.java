package com.hrm.Service.wage;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.WageCategories;
import com.hrm.Entity.wage.WageMonthly;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.wage.WageMonthlyMapper;
import com.hrm.dto.request.wage.salaryDynamicValues.WageMonthlyRequest;
import com.hrm.dto.request.wage.salaryDynamicValues.WageUpdateRequest;
import com.hrm.dto.response.wage.WageMonthlyRespone;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.WageCateRepository;
import com.hrm.repository.wage.WageMonthlyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WageMonthlyService {
    WageMonthlyRepository wageMonthlyRepository;
    WageMonthlyMapper wageMonthlyMapper;
    EmployeeRepository employeeRepository;
    WageCateRepository wageCateRepository;

    // thêm danh sách
    public List<WageMonthlyRespone> create(List<WageMonthlyRequest> requests) {
        List<WageMonthly> wagesToSave = new ArrayList<>();

        for (WageMonthlyRequest request : requests) {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
            WageCategories wageCategories = wageCateRepository.findById(request.getWageCategoriesId())
                    .orElseThrow(() -> new AppException(ErrorCode.WAGECATE_NOT_EXISTED));

            if (wageMonthlyRepository.existsByEmployeeCate(request.getEmployeeId(), request.getWageCategoriesId(), request.getTime()) == null) {
                WageMonthly wage = wageMonthlyMapper.toWageMonthly(request);
                wage.setWageCategories(wageCategories);
                wage.setEmployee(employee);
                wagesToSave.add(wage);
            }
        }

        List<WageMonthly> savedWages = wageMonthlyRepository.saveAll(wagesToSave);

        return savedWages.stream()
                .map(wageMonthlyMapper::toWageMonthlyRespone)
                .collect(Collectors.toList());
    }

    // cập nhật
    public List<WageMonthlyRespone> update(List<WageUpdateRequest> requests) {
        List<WageMonthly> wages = requests.stream().map(request -> {
            WageMonthly wage = wageMonthlyRepository.findById(request.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.WAGE_NOT_EXISTED));

            wage.setSalary(request.getSalary());
            return wage;
        }).toList();

        List<WageMonthly> savedWages = wageMonthlyRepository.saveAll(wages);
        return savedWages.stream()
                .map(wageMonthlyMapper::toWageMonthlyRespone)
                .collect(Collectors.toList());
    }

    // lấy theo id
    public List<WageMonthlyRespone> getWage(int wageIdEmployee, String time) {
        return wageMonthlyRepository.findByEmployeeId(wageIdEmployee, time)
                .stream().map(wageMonthlyMapper::toWageMonthlyRespone).toList();
    }

    // tìm kiếm
    public List<WageMonthlyRespone> searchAll(int pageNumber, int pageSize, String name, String time, Integer wageCategories, String type) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return wageMonthlyRepository.findByTimeWage(name, time, wageCategories, type, pageable)
                .stream().map(wageMonthlyMapper::toWageMonthlyRespone).toList();
    }

    public PageCustom getPagination(int pageNumber, int pageSize, String name, String time, Integer wageCategories, String type) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<WageMonthly> page = wageMonthlyRepository.findByTimeWage(name, time, wageCategories, type, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa
    public void deleteWage(int wageId) {
        wageMonthlyRepository.deleteById(wageId);
    }

}
