package com.hrm.Mapper.timekeeping;

import com.hrm.Entity.timekeeping.TimeKeeping;
import com.hrm.dto.request.timekeeping.TimeKeepingRequest;
import com.hrm.dto.response.timekeeping.TimeKeepingRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TimeKeepingMapper {
    TimeKeeping toKeeping(TimeKeepingRequest request);

    TimeKeepingRespone toKeepingRespone(TimeKeeping keeping);

    void updateKeeping(@MappingTarget TimeKeeping keeping, TimeKeepingRequest request);
}
