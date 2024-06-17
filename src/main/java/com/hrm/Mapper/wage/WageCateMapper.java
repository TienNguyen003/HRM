package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.WageCategories;
import com.hrm.dto.request.wage.WageCateRequest;
import com.hrm.dto.response.wage.WageCateRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WageCateMapper {
    WageCategories toWageCate(WageCateRequest request);

    WageCateRespone toWageCateRespone(WageCategories wageCategories);

    void updateDayOff(@MappingTarget WageCategories wageCategories, WageCateRequest request);
}
