package com.hrm.Mapper.job;

import com.hrm.Entity.job.Requirements;
import com.hrm.dto.request.job.RequirementsRequest;
import com.hrm.dto.response.job.RequirementsResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class RequirementsMapperImpl implements RequirementsMapper {

    @Override
    public Requirements toRequirements(RequirementsRequest request) {
        if ( request == null ) {
            return null;
        }

        Requirements.RequirementsBuilder requirements = Requirements.builder();

        requirements.title( request.getTitle() );
        requirements.assessmentId( request.getAssessmentId() );

        return requirements.build();
    }

    @Override
    public RequirementsResponse toRequirementsResponse(Requirements requirements) {
        if ( requirements == null ) {
            return null;
        }

        RequirementsResponse.RequirementsResponseBuilder requirementsResponse = RequirementsResponse.builder();

        requirementsResponse.id( requirements.getId() );
        requirementsResponse.title( requirements.getTitle() );
        requirementsResponse.assessmentId( requirements.getAssessmentId() );

        return requirementsResponse.build();
    }

    @Override
    public void updateRequire(Requirements requirements, RequirementsRequest request) {
        if ( request == null ) {
            return;
        }

        requirements.setTitle( request.getTitle() );
        requirements.setAssessmentId( request.getAssessmentId() );
    }
}
