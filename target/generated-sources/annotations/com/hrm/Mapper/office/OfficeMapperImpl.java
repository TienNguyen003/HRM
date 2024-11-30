package com.hrm.Mapper.office;

import com.hrm.Entity.office.OfficeI;
import com.hrm.dto.request.office.OfficeRequest;
import com.hrm.dto.response.office.OfficeRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class OfficeMapperImpl implements OfficeMapper {

    @Override
    public OfficeI toOfficeI(OfficeRequest request) {
        if ( request == null ) {
            return null;
        }

        OfficeI.OfficeIBuilder officeI = OfficeI.builder();

        officeI.name( request.getName() );
        officeI.address( request.getAddress() );
        officeI.email( request.getEmail() );
        officeI.phone( request.getPhone() );

        return officeI.build();
    }

    @Override
    public OfficeRespone toOfficeRespone(OfficeI officeI) {
        if ( officeI == null ) {
            return null;
        }

        OfficeRespone.OfficeResponeBuilder officeRespone = OfficeRespone.builder();

        officeRespone.id( officeI.getId() );
        officeRespone.name( officeI.getName() );
        officeRespone.address( officeI.getAddress() );
        officeRespone.email( officeI.getEmail() );
        officeRespone.phone( officeI.getPhone() );
        officeRespone.status( officeI.getStatus() );

        return officeRespone.build();
    }

    @Override
    public void updateOffice(OfficeI officeI, OfficeRequest request) {
        if ( request == null ) {
            return;
        }

        officeI.setName( request.getName() );
        officeI.setAddress( request.getAddress() );
        officeI.setEmail( request.getEmail() );
        officeI.setPhone( request.getPhone() );
    }
}
