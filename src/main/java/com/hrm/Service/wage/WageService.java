package com.hrm.Service.wage;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.role.Role;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Wage;
import com.hrm.Entity.wage.WageCategories;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.wage.WageMapper;
import com.hrm.dto.request.wage.WageRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WageService {
    WageRepository wageRepository;
    WageMapper wageMapper;
    EmployeeRepository employeeRepository;
    WageCateRepository wageCateRepository;

    // thêm danh sách
    public WageRespone create(WageRequest request){
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        WageCategories wageCategories = wageCateRepository.findById(request.getWageCategoriesId())
                .orElseThrow(() -> new AppException(ErrorCode.WAGECATE_NOT_EXISTED));

        Wage wage = wageMapper.toWage(request);

        wage.setWageCategories(wageCategories);
        wage.setEmployee(employee);

        wageMapper.toWageRespone(wageRepository.save(wage));

        return wageMapper.toWageRespone(wageRepository.save(wage));
    }

    // cập nhật
    public WageRespone update(int wageId, WageRequest request){
        Wage wage = wageRepository.findById(wageId)
                .orElseThrow(() -> new AppException(ErrorCode.WAGE_NOT_EXISTED));

        wageMapper.updateWage(wage, request);

        return wageMapper.toWageRespone(wageRepository.save(wage));
    }

    // lấy theo id
    public WageRespone getWage(int wageId){
        return wageMapper.toWageRespone(wageRepository.findById(wageId)
                .orElseThrow(() -> new AppException(ErrorCode.WAGE_NOT_EXISTED)));
    }

    // tìm kiếm
    public List<WageRespone> searchAll(int pageNumber, int pageSize, String name, Integer wageCategories, String type){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return wageRepository.findByNameAndWage(name, wageCategories, type, pageable)
                .stream().map(wageMapper::toWageRespone).toList();
    }

    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer wageCategories, String type){
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
    public void deleteWage(int wageId){
        wageRepository.deleteById(wageId);
    }

}
