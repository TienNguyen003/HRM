package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.DayOffCategories;
import com.hrm.dto.request.dayOff.DayOffRequest;
import com.hrm.dto.response.day_off.DayOffResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DayOffMapper {
    DayOffCategories toDayOff(DayOffRequest request);

    DayOffResponse toDayOffRespone(DayOffCategories dayOffCategories);

    void updateDayOff(@MappingTarget DayOffCategories dayOffCategories, DayOffRequest request);
}
