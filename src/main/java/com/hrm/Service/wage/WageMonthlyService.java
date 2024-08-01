package com.hrm.Service.wage;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.WageMonthly;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.wage.WageMonthlyMapper;
import com.hrm.dto.request.wage.salaryDynamicValues.WageMonthlyRequest;
import com.hrm.dto.response.wage.WageMonthlyRespone;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.WageMonthlyRepository;
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
public class WageMonthlyService {
    WageMonthlyRepository wageMonthlyRepository;
    WageMonthlyMapper wageMonthlyMapper;
    EmployeeRepository employeeRepository;

    // thêm danh sách
    public WageMonthlyRespone create(WageMonthlyRequest request){
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        WageMonthly wageMonthly = wageMonthlyMapper.toWageMonthly(request);

        wageMonthly.setEmployee(employee);

        return wageMonthlyMapper.toWageMonthlyRespone(wageMonthlyRepository.save(wageMonthly));
    }

    // cập nhật
    public WageMonthlyRespone update(int wageMonthId, WageMonthlyRequest request){
        WageMonthly wage = wageMonthlyRepository.findById(wageMonthId)
                .orElseThrow(() -> new AppException(ErrorCode.WAGE_NOT_EXISTED));

        wageMonthlyMapper.updateWage(wage, request);

        return wageMonthlyMapper.toWageMonthlyRespone(wageMonthlyRepository.save(wage));
    }

    // lấy theo id
    public List<WageMonthlyRespone> getWage(int wageIdEmployee){
        return wageMonthlyRepository.findByEmployeeId(wageIdEmployee)
                .stream().map(wageMonthlyMapper::toWageMonthlyRespone).toList();
    }

    // tìm kiếm
    public List<WageMonthlyRespone> searchAll(int pageNumber, int pageSize, String name, String time, Integer wageCategories, String type){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return wageMonthlyRepository.findByTimeWage(name, time, wageCategories, type, pageable)
                .stream().map(wageMonthlyMapper::toWageMonthlyRespone).toList();
    }
    public PageCustom getPagination(int pageNumber, int pageSize, String name, String time, Integer wageCategories, String type){
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
    public void deleteWage(int wageId){
        wageMonthlyRepository.deleteById(wageId);
    }

}
