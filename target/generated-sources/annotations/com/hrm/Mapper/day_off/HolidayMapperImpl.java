package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.Holiday;
import com.hrm.dto.request.dayOff.HolidayRequest;
import com.hrm.dto.response.day_off.HolidayResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class HolidayMapperImpl implements HolidayMapper {

    @Override
    public Holiday toHoliday(HolidayRequest request) {
        if ( request == null ) {
            return null;
        }

        Holiday.HolidayBuilder holiday = Holiday.builder();

        holiday.name( request.getName() );
        holiday.startTime( request.getStartTime() );
        holiday.endTime( request.getEndTime() );
        holiday.totalTime( request.getTotalTime() );

        return holiday.build();
    }

    @Override
    public HolidayResponse toHolidayRespone(Holiday holiday) {
        if ( holiday == null ) {
            return null;
        }

        HolidayResponse.HolidayResponseBuilder holidayResponse = HolidayResponse.builder();

        holidayResponse.id( holiday.getId() );
        holidayResponse.name( holiday.getName() );
        holidayResponse.startTime( holiday.getStartTime() );
        holidayResponse.endTime( holiday.getEndTime() );
        holidayResponse.totalTime( holiday.getTotalTime() );

        return holidayResponse.build();
    }

    @Override
    public void updateHoliday(Holiday holiday, HolidayRequest request) {
        if ( request == null ) {
            return;
        }

        holiday.setName( request.getName() );
        holiday.setStartTime( request.getStartTime() );
        holiday.setEndTime( request.getEndTime() );
        holiday.setTotalTime( request.getTotalTime() );
    }
}
