package com.hrm.Mapper.office;

import com.hrm.Entity.office.OfficeI;
import com.hrm.dto.request.office.OfficeRequest;
import com.hrm.dto.response.office.OfficeRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OfficeMapper {
    OfficeI toOfficeI(OfficeRequest request);

    OfficeRespone toOfficeRespone(OfficeI officeI);

    void updateOffice(@MappingTarget OfficeI officeI, OfficeRequest request);
}
