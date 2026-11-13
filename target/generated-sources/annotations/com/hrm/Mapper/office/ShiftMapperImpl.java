package com.hrm.Mapper.office;

import com.hrm.Entity.office.Shift;
import com.hrm.dto.request.office.ShiftRequest;
import com.hrm.dto.response.office.ShiftResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class ShiftMapperImpl implements ShiftMapper {

    @Override
    public Shift toShift(ShiftRequest request) {
        if ( request == null ) {
            return null;
        }

        Shift.ShiftBuilder shift = Shift.builder();

        shift.start( request.getStart() );
        shift.end( request.getEnd() );
        shift.workingDay( request.getWorkingDay() );

        return shift.build();
    }

    @Override
    public ShiftResponse toShiftResponse(Shift shift) {
        if ( shift == null ) {
            return null;
        }

        ShiftResponse.ShiftResponseBuilder shiftResponse = ShiftResponse.builder();

        shiftResponse.id( shift.getId() );
        shiftResponse.start( shift.getStart() );
        shiftResponse.end( shift.getEnd() );
        shiftResponse.workingDay( shift.getWorkingDay() );

        return shiftResponse.build();
    }

    @Override
    public void updateShift(Shift shift, ShiftRequest request) {
        if ( request == null ) {
            return;
        }

        shift.setStart( request.getStart() );
        shift.setEnd( request.getEnd() );
        shift.setWorkingDay( request.getWorkingDay() );
    }
}
