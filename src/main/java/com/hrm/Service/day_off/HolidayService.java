package com.hrm.Service.day_off;

import com.hrm.Entity.day_off.Holiday;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.day_off.HolidayMapper;
import com.hrm.dto.request.dayOff.HolidayRequest;
import com.hrm.dto.response.day_off.HolidayResponse;
import com.hrm.repository.day_off.HolidayRepository;
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
public class HolidayService {
    HolidayRepository holidayRepository;
    HolidayMapper holidayMapper;

    // thêm 1 ngày nghỉ vào dnah sách
    public HolidayResponse createH(HolidayRequest request){
        if(holidayRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.DAYOFF_EXISTED);

        Holiday holiday = holidayMapper.toHoliday(request);

        return holidayMapper.toHolidayRespone(holidayRepository.save(holiday));
    }

    // cập nhật
    public HolidayResponse updateH(int holidayId, HolidayRequest request){

        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_EXISTED));

        holidayMapper.updateHoliday(holiday, request);

        return holidayMapper.toHolidayRespone(holidayRepository.save(holiday));
    }

    // lấy ra tất cả
    public List<HolidayResponse> getAllH(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return holidayRepository.findAll(pageable)
                .stream().map(holidayMapper::toHolidayRespone).toList();
    }

    // lấy theo id
    public HolidayResponse getH(int datOffId){
        return holidayMapper.toHolidayRespone(holidayRepository.findById(datOffId)
                .orElseThrow(() -> new AppException(ErrorCode.DAYOFF_EXISTED)));
    }

    // tìm kiếm
    public List<HolidayResponse> searchAll(int pageNumber, int pageSize, String name){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return holidayRepository.findByName(name, pageable)
                .stream().map(holidayMapper::toHolidayRespone).toList();
    }

    // xóa ngày nghỉ
    public void deleteH(int holidayId){
        holidayRepository.deleteById(holidayId);
    }
}
