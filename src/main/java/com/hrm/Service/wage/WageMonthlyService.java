package com.hrm.Service.wage;

import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Wage;
import com.hrm.Entity.wage.WageMonthly;
import com.hrm.Mapper.wage.WageMapper;
import com.hrm.Mapper.wage.WageMonthlyMapper;
import com.hrm.dto.request.wage.WageMonthlyRequest;
import com.hrm.dto.request.wage.WageRequest;
import com.hrm.dto.response.wage.WageMonthlyRespone;
import com.hrm.dto.response.wage.WageRespone;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.wage.WageMonthlyRepository;
import com.hrm.repository.wage.WageRepository;
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
public class WageMonthlyService {
    WageMonthlyRepository wageMonthlyRepository;
    WageMonthlyMapper wageMonthlyMapper;
    EmployeeRepository employeeRepository;

    // thêm danh sách
    public WageMonthlyRespone create(WageMonthlyRequest request){
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("NO employee  not found"));

        WageMonthly wageMonthly = wageMonthlyMapper.toWageMonthly(request);

        wageMonthly.setEmployee(employee);

        return wageMonthlyMapper.toWageMonthlyRespone(wageMonthlyRepository.save(wageMonthly));
    }

    // cập nhật
    public WageMonthlyRespone update(int wageMonthId, WageMonthlyRequest request){
        WageMonthly wage = wageMonthlyRepository.findById(wageMonthId)
                .orElseThrow(() -> new RuntimeException("This salary not found"));

        wageMonthlyMapper.updateWage(wage, request);

        return wageMonthlyMapper.toWageMonthlyRespone(wageMonthlyRepository.save(wage));
    }

    // lấy ra tất cả
    public List<WageMonthlyRespone> getAllWage(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return wageMonthlyRepository.findAll(pageable)
                .stream().map(wageMonthlyMapper::toWageMonthlyRespone).toList();
    }

    // lấy theo id
    public List<WageMonthlyRespone> getWage(int wageIdEmployee){
        return wageMonthlyRepository.findByEmployeeId(wageIdEmployee)
                .stream().map(wageMonthlyMapper::toWageMonthlyRespone).toList();
    }

    // tìm kiếm
    public List<WageMonthlyRespone> searchAll(int pageNumber, int pageSize, String name, String time, String wageCategories){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return wageMonthlyRepository.findByTimeContainingAndWageCategories(name, time, wageCategories, pageable)
                .stream().map(wageMonthlyMapper::toWageMonthlyRespone).toList();
    }

    // xóa
    public void deleteWage(int wageId){
        wageMonthlyRepository.deleteById(wageId);
    }

}
