package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Wage;
import com.hrm.dto.request.wage.salaryStaticValue.WageRequest;
import com.hrm.dto.response.wage.WageRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WageMapper {
    Wage toWage(WageRequest request);

    WageRespone toWageRespone(Wage wage);

    void updateWage(@MappingTarget Wage wage, WageRequest request);
}
