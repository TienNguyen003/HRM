package com.hrm.Service.day_off;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.day_off.DayOffCategories;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.day_off.DayOffMapper;
import com.hrm.dto.request.dayOff.dayoff.DayOffRequest;
import com.hrm.dto.request.dayOff.dayoff.DayOffUpdateRequest;
import com.hrm.dto.response.day_off.DayOffResponse;
import com.hrm.repository.day_off.DayOffRepository;
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
public class DayOffService {
    DayOffRepository dayOffRepository;
    DayOffMapper dayOffMapper;

    // thêm 1 ngày nghỉ vào danh sách
    public DayOffResponse createDayOff(DayOffRequest request){
        if(dayOffRepository.existsByNameDay(request.getNameDay()))
            throw new AppException(ErrorCode.DAYOFF_EXISTED);

        DayOffCategories dayOffCategories = dayOffMapper.toDayOff(request);

        return dayOffMapper.toDayOffRespone(dayOffRepository.save(dayOffCategories));
    }

    // cập nhật
    public DayOffResponse updateDayOff(int dayOffId, DayOffUpdateRequest request){
        DayOffCategories dayOffCategories = dayOffRepository.findById(dayOffId)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_EXISTED));

        dayOffMapper.updateDayOffUp(dayOffCategories, request);

        return dayOffMapper.toDayOffRespone(dayOffRepository.save(dayOffCategories));
    }

    public String updateStt(int dayOffId, int status){
        DayOffCategories dayOffCategories = dayOffRepository.findById(dayOffId)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_EXISTED));

        dayOffCategories.setStatus(status);
        dayOffMapper.toDayOffRespone(dayOffRepository.save(dayOffCategories));

        return "Update success";
    }

    public List<DayOffResponse> getAllDayOff(){
        return dayOffRepository.getAllDayOff()
                .stream().map(dayOffMapper::toDayOffRespone).toList();
    }

    // lấy theo id
    public DayOffResponse getDayOff(int datOffId){
        return dayOffMapper.toDayOffRespone(dayOffRepository.findById(datOffId)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_NOT_EXISTED)));
    }

    // tìm kiếm
    public List<DayOffResponse> searchAll(int pageNumber, int pageSize, String name, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return dayOffRepository.findByNameAndStatus(name, status, pageable)
                .stream().map(dayOffMapper::toDayOffRespone).toList();
    }
    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<DayOffCategories> page = dayOffRepository.findByNameAndStatus(name, status, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa ngày nghỉ
    public void deleteDayOff(int dayOffId){
        dayOffRepository.deleteById(dayOffId);
    }
}
