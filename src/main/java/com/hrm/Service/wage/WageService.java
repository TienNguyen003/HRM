package com.hrm.Service.wage;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Wage;
import com.hrm.Entity.wage.WageCategories;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.wage.WageMapper;
import com.hrm.dto.request.wage.salaryStaticValue.WageRequest;
import com.hrm.dto.request.wage.salaryStaticValue.WageUpdateRequest;
import com.hrm.dto.response.wage.WageRespone;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.WageCateRepository;
import com.hrm.repository.wage.WageRepository;
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
public class WageService {
    WageRepository wageRepository;
    WageMapper wageMapper;
    EmployeeRepository employeeRepository;
    WageCateRepository wageCateRepository;

    // thêm danh sách
    public List<WageRespone> create(List<WageRequest> requests) {
        List<Wage> wagesToSave = new ArrayList<>();

        for (WageRequest request : requests) {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
            WageCategories wageCategories = wageCateRepository.findById(request.getWageCategoriesId())
                    .orElseThrow(() -> new AppException(ErrorCode.WAGECATE_NOT_EXISTED));

            if (wageRepository.existsByEmployeeCate(request.getEmployeeId(), request.getWageCategoriesId()) == null) {
                Wage wage = wageMapper.toWage(request);
                wage.setWageCategories(wageCategories);
                wage.setEmployee(employee);
                wagesToSave.add(wage);
            }
        }

        List<Wage> savedWages = wageRepository.saveAll(wagesToSave);

        return savedWages.stream()
                .map(wageMapper::toWageRespone)
                .collect(Collectors.toList());
    }

    // cập nhật
    public List<WageRespone> update(List<WageUpdateRequest> requests) {
        List<Wage> wages = requests.stream().map(request -> {
            Wage wage = wageRepository.findById(request.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.WAGE_NOT_EXISTED));

            wage.setSalary(request.getSalary());
            return wage;
        }).toList();

        List<Wage> savedWages = wageRepository.saveAll(wages);
        return savedWages.stream()
                .map(wageMapper::toWageRespone)
                .collect(Collectors.toList());
    }

    // lấy theo id
    public List<WageRespone> getWage(Integer employeeId) {
        return wageRepository.findByEmployee(employeeId)
                .stream().map(wageMapper::toWageRespone).toList();
    }

    // tìm kiếm
    public List<WageRespone> searchAll(int pageNumber, int pageSize, String name, Integer wageCategories, String type) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return wageRepository.findByNameAndWage(name, wageCategories, type, pageable)
                .stream().map(wageMapper::toWageRespone).toList();
    }

    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer wageCategories, String type) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 30);
        Page<Wage> page = wageRepository.findByNameAndWage(name, wageCategories, type, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa
    public void deleteWage(int wageId) {
        wageRepository.deleteById(wageId);
    }

}
