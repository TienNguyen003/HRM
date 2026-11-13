package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.WageCategories;
import com.hrm.dto.request.wage.WageCateRequest;
import com.hrm.dto.response.wage.WageCateRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
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
        wageCategories.salaryType( request.getSalaryType() );
        wageCategories.symbol( request.getSymbol() );

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
        wageCateRespone.salaryType( wageCategories.getSalaryType() );
        wageCateRespone.symbol( wageCategories.getSymbol() );

        return wageCateRespone.build();
    }

    @Override
    public void updateDayOff(WageCategories wageCategories, WageCateRequest request) {
        if ( request == null ) {
            return;
        }

        wageCategories.setName( request.getName() );
        wageCategories.setSalaryType( request.getSalaryType() );
        wageCategories.setSymbol( request.getSymbol() );
    }
}
