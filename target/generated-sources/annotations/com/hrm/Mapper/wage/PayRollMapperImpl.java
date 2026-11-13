package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Payroll;
import com.hrm.dto.request.wage.payroll.PayrollRequest;
import com.hrm.dto.response.wage.PayrollRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class PayRollMapperImpl implements PayRollMapper {

    @Override
    public Payroll toPayRoll(PayrollRequest request) {
        if ( request == null ) {
            return null;
        }

        Payroll.PayrollBuilder payroll = Payroll.builder();

        payroll.advance( request.getAdvance() );
        payroll.status( request.getStatus() );
        payroll.time( request.getTime() );
        payroll.totalSalary( request.getTotalSalary() );

        return payroll.build();
    }

    @Override
    public PayrollRespone toPayRollRespone(Payroll payroll) {
        if ( payroll == null ) {
            return null;
        }

        PayrollRespone.PayrollResponeBuilder payrollRespone = PayrollRespone.builder();

        payrollRespone.advance( payroll.getAdvance() );
        payrollRespone.bank( payroll.getBank() );
        payrollRespone.employee( payroll.getEmployee() );
        payrollRespone.id( payroll.getId() );
        payrollRespone.status( payroll.getStatus() );
        payrollRespone.time( payroll.getTime() );
        payrollRespone.totalSalary( payroll.getTotalSalary() );

        return payrollRespone.build();
    }

    @Override
    public void updatePayRoll(Payroll payroll, PayrollRequest request) {
        if ( request == null ) {
            return;
        }

        payroll.setAdvance( request.getAdvance() );
        payroll.setStatus( request.getStatus() );
        payroll.setTime( request.getTime() );
        payroll.setTotalSalary( request.getTotalSalary() );
    }
}
