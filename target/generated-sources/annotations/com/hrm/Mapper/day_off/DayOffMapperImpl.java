package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.DayOffCategories;
import com.hrm.dto.request.dayOff.dayoff.DayOffRequest;
import com.hrm.dto.request.dayOff.dayoff.DayOffUpdateRequest;
import com.hrm.dto.response.day_off.DayOffResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class DayOffMapperImpl implements DayOffMapper {

    @Override
    public DayOffCategories toDayOff(DayOffRequest request) {
        if ( request == null ) {
            return null;
        }

        DayOffCategories.DayOffCategoriesBuilder dayOffCategories = DayOffCategories.builder();

        dayOffCategories.nameDay( request.getNameDay() );
        dayOffCategories.status( request.getStatus() );
        dayOffCategories.timeDay( request.getTimeDay() );
        dayOffCategories.timeUpdate( request.getTimeUpdate() );

        return dayOffCategories.build();
    }

    @Override
    public DayOffResponse toDayOffRespone(DayOffCategories dayOffCategories) {
        if ( dayOffCategories == null ) {
            return null;
        }

        DayOffResponse.DayOffResponseBuilder dayOffResponse = DayOffResponse.builder();

        dayOffResponse.id( dayOffCategories.getId() );
        dayOffResponse.nameDay( dayOffCategories.getNameDay() );
        dayOffResponse.status( dayOffCategories.getStatus() );
        dayOffResponse.timeDay( dayOffCategories.getTimeDay() );
        dayOffResponse.timeUpdate( dayOffCategories.getTimeUpdate() );

        return dayOffResponse.build();
    }

    @Override
    public void updateDayOff(DayOffCategories dayOffCategories, DayOffRequest request) {
        if ( request == null ) {
            return;
        }

        dayOffCategories.setNameDay( request.getNameDay() );
        dayOffCategories.setStatus( request.getStatus() );
        dayOffCategories.setTimeDay( request.getTimeDay() );
        dayOffCategories.setTimeUpdate( request.getTimeUpdate() );
    }

    @Override
    public void updateDayOffUp(DayOffCategories dayOffCategories, DayOffUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        dayOffCategories.setNameDay( request.getNameDay() );
        dayOffCategories.setTimeDay( request.getTimeDay() );
        dayOffCategories.setTimeUpdate( request.getTimeUpdate() );
    }
}
