package com.hrm.Mapper.job;

import com.hrm.Entity.job.Requirements;
import com.hrm.dto.request.job.RequirementsRequest;
import com.hrm.dto.response.job.RequirementsResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class RequirementsMapperImpl implements RequirementsMapper {

    @Override
    public Requirements toRequirements(RequirementsRequest request) {
        if ( request == null ) {
            return null;
        }

        Requirements.RequirementsBuilder requirements = Requirements.builder();

        requirements.assessmentId( request.getAssessmentId() );
        requirements.title( request.getTitle() );

        return requirements.build();
    }

    @Override
    public RequirementsResponse toRequirementsResponse(Requirements requirements) {
        if ( requirements == null ) {
            return null;
        }

        RequirementsResponse.RequirementsResponseBuilder requirementsResponse = RequirementsResponse.builder();

        requirementsResponse.assessmentId( requirements.getAssessmentId() );
        requirementsResponse.id( requirements.getId() );
        requirementsResponse.title( requirements.getTitle() );

        return requirementsResponse.build();
    }

    @Override
    public void updateRequire(Requirements requirements, RequirementsRequest request) {
        if ( request == null ) {
            return;
        }

        requirements.setAssessmentId( request.getAssessmentId() );
        requirements.setTitle( request.getTitle() );
    }
}
