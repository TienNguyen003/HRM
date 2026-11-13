package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.WageMonthly;
import com.hrm.dto.request.wage.salaryDynamicValues.WageMonthlyRequest;
import com.hrm.dto.response.wage.WageMonthlyRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class WageMonthlyMapperImpl implements WageMonthlyMapper {

    @Override
    public WageMonthly toWageMonthly(WageMonthlyRequest request) {
        if ( request == null ) {
            return null;
        }

        WageMonthly.WageMonthlyBuilder wageMonthly = WageMonthly.builder();

        wageMonthly.time( request.getTime() );
        wageMonthly.salary( request.getSalary() );

        return wageMonthly.build();
    }

    @Override
    public WageMonthlyRespone toWageMonthlyRespone(WageMonthly wageMonthly) {
        if ( wageMonthly == null ) {
            return null;
        }

        WageMonthlyRespone.WageMonthlyResponeBuilder wageMonthlyRespone = WageMonthlyRespone.builder();

        wageMonthlyRespone.id( wageMonthly.getId() );
        wageMonthlyRespone.time( wageMonthly.getTime() );
        wageMonthlyRespone.salary( wageMonthly.getSalary() );
        wageMonthlyRespone.wageCategories( wageMonthly.getWageCategories() );
        wageMonthlyRespone.employee( wageMonthly.getEmployee() );

        return wageMonthlyRespone.build();
    }

    @Override
    public void updateWage(WageMonthly wageMonthly, WageMonthlyRequest request) {
        if ( request == null ) {
            return;
        }

        wageMonthly.setTime( request.getTime() );
        wageMonthly.setSalary( request.getSalary() );
    }
}
