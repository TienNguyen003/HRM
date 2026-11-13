package com.hrm.Mapper.job;

import com.hrm.Entity.job.Assessment;
import com.hrm.dto.request.job.AssessmentRequest;
import com.hrm.dto.response.job.AssessmentResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class AssessmentMapperImpl implements AssessmentMapper {

    @Override
    public Assessment toAssessment(AssessmentRequest request) {
        if ( request == null ) {
            return null;
        }

        Assessment.AssessmentBuilder assessment = Assessment.builder();

        assessment.requirement( request.getRequirement() );
        assessment.title( request.getTitle() );

        return assessment.build();
    }

    @Override
    public AssessmentResponse toAssessmentResponse(Assessment assessment) {
        if ( assessment == null ) {
            return null;
        }

        AssessmentResponse.AssessmentResponseBuilder assessmentResponse = AssessmentResponse.builder();

        assessmentResponse.id( assessment.getId() );
        assessmentResponse.requirement( assessment.getRequirement() );
        assessmentResponse.title( assessment.getTitle() );

        return assessmentResponse.build();
    }

    @Override
    public void updateAssessment(Assessment assessment, AssessmentRequest request) {
        if ( request == null ) {
            return;
        }

        assessment.setRequirement( request.getRequirement() );
        assessment.setTitle( request.getTitle() );
    }
}
