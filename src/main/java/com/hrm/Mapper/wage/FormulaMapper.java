package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Formula;
import com.hrm.dto.request.wage.FormulaRequest;
import com.hrm.dto.response.wage.FormulaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FormulaMapper {
    Formula toFormula(FormulaRequest request);

    FormulaResponse toFormulaResponse(Formula formula);

    void updateFormula(@MappingTarget Formula formula, FormulaRequest request);
}
