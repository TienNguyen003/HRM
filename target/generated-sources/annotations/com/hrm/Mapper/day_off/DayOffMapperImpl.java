package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.DayOffCategories;
import com.hrm.dto.request.dayOff.dayoff.DayOffRequest;
import com.hrm.dto.request.dayOff.dayoff.DayOffUpdateRequest;
import com.hrm.dto.response.day_off.DayOffResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
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
        dayOffCategories.timeDay( request.getTimeDay() );
        dayOffCategories.timeUpdate( request.getTimeUpdate() );
        dayOffCategories.status( request.getStatus() );

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
        dayOffResponse.timeDay( dayOffCategories.getTimeDay() );
        dayOffResponse.timeUpdate( dayOffCategories.getTimeUpdate() );
        dayOffResponse.status( dayOffCategories.getStatus() );

        return dayOffResponse.build();
    }

    @Override
    public void updateDayOff(DayOffCategories dayOffCategories, DayOffRequest request) {
        if ( request == null ) {
            return;
        }

        dayOffCategories.setNameDay( request.getNameDay() );
        dayOffCategories.setTimeDay( request.getTimeDay() );
        dayOffCategories.setTimeUpdate( request.getTimeUpdate() );
        dayOffCategories.setStatus( request.getStatus() );
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
