package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.Holiday;
import com.hrm.dto.request.dayOff.HolidayRequest;
import com.hrm.dto.response.day_off.HolidayResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HolidayMapper {
    Holiday toHoliday(HolidayRequest request);

    HolidayResponse toHolidayRespone(Holiday holiday);

    void updateHoliday(@MappingTarget Holiday holiday, HolidayRequest request);
}
