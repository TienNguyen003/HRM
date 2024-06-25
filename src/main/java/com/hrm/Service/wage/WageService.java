package com.hrm.Service.wage;

import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.Wage;
import com.hrm.Mapper.wage.WageMapper;
import com.hrm.dto.request.wage.WageRequest;
import com.hrm.dto.response.wage.WageRespone;
import com.hrm.repository.user.EmployeeRepository;
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
public class WageService {
    WageRepository wageRepository;
    WageMapper wageMapper;
    EmployeeRepository employeeRepository;

    // thêm danh sách
    public WageRespone create(WageRequest request){
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("No employee  not found"));

        Wage wage = wageMapper.toWage(request);

        wage.setEmployee(employee);

        wageMapper.toWageRespone(wageRepository.save(wage));

        return wageMapper.toWageRespone(wageRepository.save(wage));
    }

    // cập nhật
    public WageRespone update(int wageId, WageRequest request){
        Wage wage = wageRepository.findById(wageId)
                .orElseThrow(() -> new RuntimeException("This wage not found"));

        wageMapper.updateWage(wage, request);

        return wageMapper.toWageRespone(wageRepository.save(wage));
    }

    // lấy ra tất cả
    public List<WageRespone> getAllWage(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return wageRepository.findAll(pageable)
                .stream().map(wageMapper::toWageRespone).toList();
    }

    // lấy theo id
    public WageRespone getWage(int wageId){
        return wageMapper.toWageRespone(wageRepository.findById(wageId)
                .orElseThrow(() -> new RuntimeException("This wage not found")));
    }

    // tìm kiếm
    public List<WageRespone> searchAll(int pageNumber, int pageSize, String name, String wageCategories){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return wageRepository.findByNameContainingAndWageCategories(name, wageCategories, pageable)
                .stream().map(wageMapper::toWageRespone).toList();
    }

    // xóa
    public void deleteWage(int wageId){
        wageRepository.deleteById(wageId);
    }

}
