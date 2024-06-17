package com.hrm.Service.day_off;

import com.hrm.Entity.day_off.DayOffCategories;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.day_off.DayOffMapper;
import com.hrm.dto.request.dayOff.DayOffRequest;
import com.hrm.dto.response.day_off.DayOffResponse;
import com.hrm.repository.day_off.DayOffRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DayOffService {
    DayOffRepository dayOffRepository;
    DayOffMapper dayOffMapper;

    // thêm 1 ngày nghỉ vào dnah sách
    public DayOffResponse createDayOff(DayOffRequest request){
        if(dayOffRepository.existsByNameDay(request.getNameDay()))
            throw new AppException(ErrorCode.DayOff_EXISTED);

        DayOffCategories dayOffCategories = dayOffMapper.toDayOff(request);

        return dayOffMapper.toDayOffRespone(dayOffRepository.save(dayOffCategories));
    }

    // cập nhật
    public DayOffResponse updateDayOff(int dayOffId, DayOffRequest request){

        DayOffCategories dayOffCategories = dayOffRepository.findById(dayOffId)
                .orElseThrow(() -> new RuntimeException("Day off not found"));

        dayOffMapper.updateDayOff(dayOffCategories, request);

        return dayOffMapper.toDayOffRespone(dayOffRepository.save(dayOffCategories));
    }

    // lấy ra tất cả
    public List<DayOffResponse> getAllDayOff(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return dayOffRepository.findAll(pageable)
                .stream().map(dayOffMapper::toDayOffRespone).toList();
    }

    // lấy theo id
    @PostAuthorize("returnObject.username == authentication.name")
    public DayOffResponse getDayOff(int datOffId){
        return dayOffMapper.toDayOffRespone(dayOffRepository.findById(datOffId)
                .orElseThrow(() -> new RuntimeException("Day off not found")));
    }

    // tìm kiếm
    public List<DayOffResponse> searchAll(int pageNumber, int pageSize, String name, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return dayOffRepository.findByNameDayContainingAndStatus(name, status, pageable)
                .stream().map(dayOffMapper::toDayOffRespone).toList();
    }

    // xóa ngày nghỉ
    public void deleteDayOff(int dayOffId){
        dayOffRepository.deleteById(dayOffId);
    }
}
