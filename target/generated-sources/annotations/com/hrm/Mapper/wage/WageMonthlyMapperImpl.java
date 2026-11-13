package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.WageMonthly;
import com.hrm.dto.request.wage.salaryDynamicValues.WageMonthlyRequest;
import com.hrm.dto.response.wage.WageMonthlyRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class WageMonthlyMapperImpl implements WageMonthlyMapper {

    @Override
    public WageMonthly toWageMonthly(WageMonthlyRequest request) {
        if ( request == null ) {
            return null;
        }

        WageMonthly.WageMonthlyBuilder wageMonthly = WageMonthly.builder();

        wageMonthly.salary( request.getSalary() );
        wageMonthly.time( request.getTime() );

        return wageMonthly.build();
    }

    @Override
    public WageMonthlyRespone toWageMonthlyRespone(WageMonthly wageMonthly) {
        if ( wageMonthly == null ) {
            return null;
        }

        WageMonthlyRespone.WageMonthlyResponeBuilder wageMonthlyRespone = WageMonthlyRespone.builder();

        wageMonthlyRespone.employee( wageMonthly.getEmployee() );
        wageMonthlyRespone.id( wageMonthly.getId() );
        wageMonthlyRespone.salary( wageMonthly.getSalary() );
        wageMonthlyRespone.time( wageMonthly.getTime() );
        wageMonthlyRespone.wageCategories( wageMonthly.getWageCategories() );

        return wageMonthlyRespone.build();
    }

    @Override
    public void updateWage(WageMonthly wageMonthly, WageMonthlyRequest request) {
        if ( request == null ) {
            return;
        }

        wageMonthly.setSalary( request.getSalary() );
        wageMonthly.setTime( request.getTime() );
    }
}
