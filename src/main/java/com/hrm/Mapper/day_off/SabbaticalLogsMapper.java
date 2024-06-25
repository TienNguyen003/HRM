package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.SabbaticalLeaveLogs;
import com.hrm.dto.request.dayOff.SabbaticalLogsRequest;
import com.hrm.dto.response.day_off.SabbaticalLogsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SabbaticalLogsMapper {
    SabbaticalLeaveLogs toSabLogs(SabbaticalLogsRequest request);

    SabbaticalLogsResponse toSabLogsResponse(SabbaticalLeaveLogs dayOffCategories);

    void updateSabLogs(@MappingTarget SabbaticalLeaveLogs sabbaticalLeaveLogs, SabbaticalLogsRequest request);
}
