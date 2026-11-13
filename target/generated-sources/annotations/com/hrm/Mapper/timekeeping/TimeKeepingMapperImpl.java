package com.hrm.Mapper.timekeeping;

import com.hrm.Entity.timekeeping.TimeKeeping;
import com.hrm.dto.request.timekeeping.TimeKeepingRequest;
import com.hrm.dto.response.timekeeping.TimeKeepingRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class TimeKeepingMapperImpl implements TimeKeepingMapper {

    @Override
    public TimeKeeping toKeeping(TimeKeepingRequest request) {
        if ( request == null ) {
            return null;
        }

        TimeKeeping.TimeKeepingBuilder timeKeeping = TimeKeeping.builder();

        timeKeeping.date( request.getDate() );
        timeKeeping.reason( request.getReason() );
        timeKeeping.time( request.getTime() );
        timeKeeping.type( request.getType() );

        return timeKeeping.build();
    }

    @Override
    public TimeKeepingRespone toKeepingRespone(TimeKeeping keeping) {
        if ( keeping == null ) {
            return null;
        }

        TimeKeepingRespone.TimeKeepingResponeBuilder timeKeepingRespone = TimeKeepingRespone.builder();

        timeKeepingRespone.date( keeping.getDate() );
        timeKeepingRespone.employee( keeping.getEmployee() );
        timeKeepingRespone.id( keeping.getId() );
        timeKeepingRespone.reason( keeping.getReason() );
        timeKeepingRespone.time( keeping.getTime() );
        timeKeepingRespone.type( keeping.getType() );

        return timeKeepingRespone.build();
    }

    @Override
    public void updateKeeping(TimeKeeping keeping, TimeKeepingRequest request) {
        if ( request == null ) {
            return;
        }

        keeping.setDate( request.getDate() );
        keeping.setReason( request.getReason() );
        keeping.setTime( request.getTime() );
        keeping.setType( request.getType() );
    }
}
