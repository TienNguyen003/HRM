package com.hrm.Mapper.timekeeping;

import com.hrm.Entity.timekeeping.TimeKeeping;
import com.hrm.dto.request.timekeeping.TimeKeepingRequest;
import com.hrm.dto.response.timekeeping.TimeKeepingRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class TimeKeepingMapperImpl implements TimeKeepingMapper {

    @Override
    public TimeKeeping toKeeping(TimeKeepingRequest request) {
        if ( request == null ) {
            return null;
        }

        TimeKeeping.TimeKeepingBuilder timeKeeping = TimeKeeping.builder();

        timeKeeping.time( request.getTime() );
        timeKeeping.date( request.getDate() );
        timeKeeping.reason( request.getReason() );
        timeKeeping.type( request.getType() );

        return timeKeeping.build();
    }

    @Override
    public TimeKeepingRespone toKeepingRespone(TimeKeeping keeping) {
        if ( keeping == null ) {
            return null;
        }

        TimeKeepingRespone.TimeKeepingResponeBuilder timeKeepingRespone = TimeKeepingRespone.builder();

        timeKeepingRespone.id( keeping.getId() );
        timeKeepingRespone.time( keeping.getTime() );
        timeKeepingRespone.date( keeping.getDate() );
        timeKeepingRespone.reason( keeping.getReason() );
        timeKeepingRespone.type( keeping.getType() );
        timeKeepingRespone.employee( keeping.getEmployee() );

        return timeKeepingRespone.build();
    }

    @Override
    public void updateKeeping(TimeKeeping keeping, TimeKeepingRequest request) {
        if ( request == null ) {
            return;
        }

        keeping.setTime( request.getTime() );
        keeping.setDate( request.getDate() );
        keeping.setReason( request.getReason() );
        keeping.setType( request.getType() );
    }
}
