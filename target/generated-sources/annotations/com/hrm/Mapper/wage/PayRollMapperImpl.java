package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Payroll;
import com.hrm.dto.request.wage.payroll.PayrollRequest;
import com.hrm.dto.response.wage.PayrollRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class PayRollMapperImpl implements PayRollMapper {

    @Override
    public Payroll toPayRoll(PayrollRequest request) {
        if ( request == null ) {
            return null;
        }

        Payroll.PayrollBuilder payroll = Payroll.builder();

        payroll.time( request.getTime() );
        payroll.advance( request.getAdvance() );
        payroll.totalSalary( request.getTotalSalary() );
        payroll.status( request.getStatus() );

        return payroll.build();
    }

    @Override
    public PayrollRespone toPayRollRespone(Payroll payroll) {
        if ( payroll == null ) {
            return null;
        }

        PayrollRespone.PayrollResponeBuilder payrollRespone = PayrollRespone.builder();

        payrollRespone.id( payroll.getId() );
        payrollRespone.time( payroll.getTime() );
        payrollRespone.advance( payroll.getAdvance() );
        payrollRespone.totalSalary( payroll.getTotalSalary() );
        payrollRespone.status( payroll.getStatus() );
        payrollRespone.employee( payroll.getEmployee() );
        payrollRespone.bank( payroll.getBank() );

        return payrollRespone.build();
    }

    @Override
    public void updatePayRoll(Payroll payroll, PayrollRequest request) {
        if ( request == null ) {
            return;
        }

        payroll.setTime( request.getTime() );
        payroll.setAdvance( request.getAdvance() );
        payroll.setTotalSalary( request.getTotalSalary() );
        payroll.setStatus( request.getStatus() );
    }
}
