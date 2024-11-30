package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.WageCategories;
import com.hrm.dto.request.wage.WageCateRequest;
import com.hrm.dto.response.wage.WageCateRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class WageCateMapperImpl implements WageCateMapper {

    @Override
    public WageCategories toWageCate(WageCateRequest request) {
        if ( request == null ) {
            return null;
        }

        WageCategories.WageCategoriesBuilder wageCategories = WageCategories.builder();

        wageCategories.name( request.getName() );
        wageCategories.symbol( request.getSymbol() );
        wageCategories.salaryType( request.getSalaryType() );

        return wageCategories.build();
    }

    @Override
    public WageCateRespone toWageCateRespone(WageCategories wageCategories) {
        if ( wageCategories == null ) {
            return null;
        }

        WageCateRespone.WageCateResponeBuilder wageCateRespone = WageCateRespone.builder();

        wageCateRespone.id( wageCategories.getId() );
        wageCateRespone.name( wageCategories.getName() );
        wageCateRespone.symbol( wageCategories.getSymbol() );
        wageCateRespone.salaryType( wageCategories.getSalaryType() );

        return wageCateRespone.build();
    }

    @Override
    public void updateDayOff(WageCategories wageCategories, WageCateRequest request) {
        if ( request == null ) {
            return;
        }

        wageCategories.setName( request.getName() );
        wageCategories.setSymbol( request.getSymbol() );
        wageCategories.setSalaryType( request.getSalaryType() );
    }
}
