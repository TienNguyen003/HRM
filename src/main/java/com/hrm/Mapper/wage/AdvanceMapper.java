package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Advance;
import com.hrm.dto.request.wage.advance.AdvanceRequest;
import com.hrm.dto.request.wage.advance.AdvanceUpdateSttRequest;
import com.hrm.dto.response.wage.AdvanceRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdvanceMapper {


    Advance toAdvance(AdvanceRequest request);

    Advance toAdvanceStt(AdvanceUpdateSttRequest request);

    AdvanceRespone toAdvanceRespone(Advance advance);

    void updateAdvance(@MappingTarget Advance advance, AdvanceRequest request);

    void updateAdvanceStt(@MappingTarget Advance advance, AdvanceUpdateSttRequest request);
}
