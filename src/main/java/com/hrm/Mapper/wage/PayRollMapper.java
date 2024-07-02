package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Payroll;
import com.hrm.dto.request.wage.PayrollRequest;
import com.hrm.dto.response.wage.PayrollRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PayRollMapper {


    Payroll toPayRoll(PayrollRequest request);

    PayrollRespone toPayRollRespone(Payroll payroll);

    void updatePayRoll(@MappingTarget Payroll payroll, PayrollRequest request);
}
