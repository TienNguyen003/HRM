package com.hrm.Service.wage;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.wage.Wage;
import com.hrm.Entity.wage.WageCategories;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.wage.WageCateMapper;
import com.hrm.dto.request.wage.WageCateRequest;
import com.hrm.dto.response.wage.WageCateRespone;
import com.hrm.repository.wage.WageCateRepository;
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
public class WageCateService {
    WageCateRepository wageCateRepository;
    WageCateMapper wageCateMapper;

    // thêm danh sách
    public WageCateRespone create(WageCateRequest request){
        if(wageCateRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.DAYOFF_EXISTED);

        WageCategories wageCategories = wageCateMapper.toWageCate(request);
        return wageCateMapper.toWageCateRespone(wageCateRepository.save(wageCategories));
    }

    // cập nhật
    public WageCateRespone update(int wageCateId, WageCateRequest request){
        WageCategories wageCategories = wageCateRepository.findById(wageCateId)
                .orElseThrow(() -> new AppException(ErrorCode.WAGECATE_EXISTED));

        wageCateMapper.updateDayOff(wageCategories, request);

        return wageCateMapper.toWageCateRespone(wageCateRepository.save(wageCategories));
    }

    // lấy ra tất cả
    public List<WageCateRespone> getType(String type){
        return wageCateRepository.findBySalaryType(type)
                .stream().map(wageCateMapper::toWageCateRespone).toList();
    }

    // lấy theo id
    public WageCateRespone getWageCate(int wageCateId){
        return wageCateMapper.toWageCateRespone(wageCateRepository.findById(wageCateId)
                .orElseThrow(() -> new AppException(ErrorCode.WAGECATE_EXISTED)));
    }

    // tìm kiếm
    public List<WageCateRespone> searchAll(int pageNumber, int pageSize, String name, String symbol, String salaryType){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return wageCateRepository.findByNameSymbolType(name, symbol, salaryType, pageable)
                .stream().map(wageCateMapper::toWageCateRespone).toList();
    }
    public PageCustom getPagination(int pageNumber, int pageSize, String name, String symbol, String salaryType) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 30);
        Page<WageCategories> page = wageCateRepository.findByNameSymbolType(name, symbol, salaryType, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa
    public void deleteWageCate(int wageCateId){
        wageCateRepository.deleteById(wageCateId);
    }
}
