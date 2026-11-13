package com.hrm.Mapper.office;

import com.hrm.Entity.office.Shift;
import com.hrm.dto.request.office.ShiftRequest;
import com.hrm.dto.response.office.ShiftResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ShiftMapperImpl implements ShiftMapper {

    @Override
    public Shift toShift(ShiftRequest request) {
        if ( request == null ) {
            return null;
        }

        Shift.ShiftBuilder shift = Shift.builder();

        shift.end( request.getEnd() );
        shift.start( request.getStart() );
        shift.workingDay( request.getWorkingDay() );

        return shift.build();
    }

    @Override
    public ShiftResponse toShiftResponse(Shift shift) {
        if ( shift == null ) {
            return null;
        }

        ShiftResponse.ShiftResponseBuilder shiftResponse = ShiftResponse.builder();

        shiftResponse.end( shift.getEnd() );
        shiftResponse.id( shift.getId() );
        shiftResponse.start( shift.getStart() );
        shiftResponse.workingDay( shift.getWorkingDay() );

        return shiftResponse.build();
    }

    @Override
    public void updateShift(Shift shift, ShiftRequest request) {
        if ( request == null ) {
            return;
        }

        shift.setEnd( request.getEnd() );
        shift.setStart( request.getStart() );
        shift.setWorkingDay( request.getWorkingDay() );
    }
}
