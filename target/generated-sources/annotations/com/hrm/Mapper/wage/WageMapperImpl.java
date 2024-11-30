package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Wage;
import com.hrm.dto.request.wage.salaryStaticValue.WageRequest;
import com.hrm.dto.response.wage.WageRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
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

        wageRespone.id( wage.getId() );
        wageRespone.salary( wage.getSalary() );
        wageRespone.wageCategories( wage.getWageCategories() );
        wageRespone.employee( wage.getEmployee() );

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
