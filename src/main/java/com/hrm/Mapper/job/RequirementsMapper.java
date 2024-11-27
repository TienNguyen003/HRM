package com.hrm.Mapper.job;

import com.hrm.Entity.job.Requirements;
import com.hrm.dto.request.job.RequirementsRequest;
import com.hrm.dto.response.job.RequirementsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RequirementsMapper {
    Requirements toRequirements(RequirementsRequest request);

    RequirementsResponse toRequirementsResponse(Requirements requirements);

    void updateRequire(@MappingTarget Requirements requirements, RequirementsRequest request);
}
