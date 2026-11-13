package com.hrm.Mapper.office;

import com.hrm.Entity.office.OfficeI;
import com.hrm.dto.request.office.OfficeRequest;
import com.hrm.dto.response.office.OfficeRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class OfficeMapperImpl implements OfficeMapper {

    @Override
    public OfficeI toOfficeI(OfficeRequest request) {
        if ( request == null ) {
            return null;
        }

        OfficeI.OfficeIBuilder officeI = OfficeI.builder();

        officeI.address( request.getAddress() );
        officeI.email( request.getEmail() );
        officeI.name( request.getName() );
        officeI.phone( request.getPhone() );

        return officeI.build();
    }

    @Override
    public OfficeRespone toOfficeRespone(OfficeI officeI) {
        if ( officeI == null ) {
            return null;
        }

        OfficeRespone.OfficeResponeBuilder officeRespone = OfficeRespone.builder();

        officeRespone.address( officeI.getAddress() );
        officeRespone.email( officeI.getEmail() );
        officeRespone.id( officeI.getId() );
        officeRespone.name( officeI.getName() );
        officeRespone.phone( officeI.getPhone() );
        officeRespone.status( officeI.getStatus() );

        return officeRespone.build();
    }

    @Override
    public void updateOffice(OfficeI officeI, OfficeRequest request) {
        if ( request == null ) {
            return;
        }

        officeI.setAddress( request.getAddress() );
        officeI.setEmail( request.getEmail() );
        officeI.setName( request.getName() );
        officeI.setPhone( request.getPhone() );
    }
}
