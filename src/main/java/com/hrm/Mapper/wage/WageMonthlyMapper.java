package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.WageMonthly;
import com.hrm.dto.request.wage.WageMonthlyRequest;
import com.hrm.dto.response.wage.WageMonthlyRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WageMonthlyMapper {
    WageMonthly toWageMonthly(WageMonthlyRequest request);

    WageMonthlyRespone toWageMonthlyRespone(WageMonthly wageMonthly);

    void updateWage(@MappingTarget WageMonthly wageMonthly, WageMonthlyRequest request);
}
