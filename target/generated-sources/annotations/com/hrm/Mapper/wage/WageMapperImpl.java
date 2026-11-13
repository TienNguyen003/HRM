package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Wage;
import com.hrm.dto.request.wage.salaryStaticValue.WageRequest;
import com.hrm.dto.response.wage.WageRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class WageMapperImpl implements WageMapper {

    @Override
    public Wage toWage(WageRequest request) {
        if ( request == null ) {
            return null;
        }

        Wage.WageBuilder wage = Wage.builder();

        wage.salary( request.getSalary() );

        return wage.build();
    }

    @Override
    public WageRespone toWageRespone(Wage wage) {
        if ( wage == null ) {
            return null;
        }

        WageRespone.WageResponeBuilder wageRespone = WageRespone.builder();

        wageRespone.employee( wage.getEmployee() );
        wageRespone.id( wage.getId() );
        wageRespone.salary( wage.getSalary() );
        wageRespone.wageCategories( wage.getWageCategories() );

        return wageRespone.build();
    }

    @Override
    public void updateWage(Wage wage, WageRequest request) {
        if ( request == null ) {
            return;
        }

        wage.setSalary( request.getSalary() );
    }
}
