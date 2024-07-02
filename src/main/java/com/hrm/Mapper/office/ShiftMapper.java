package com.hrm.Mapper.office;

import com.hrm.Entity.office.Shift;
import com.hrm.dto.request.office.ShiftRequest;
import com.hrm.dto.response.office.ShiftResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShiftMapper {
    Shift toShift(ShiftRequest request);

    ShiftResponse toShiftResponse(Shift shift);

    void updateShift(@MappingTarget Shift shift, ShiftRequest request);
}
