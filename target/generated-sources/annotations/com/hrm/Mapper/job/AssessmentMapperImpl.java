package com.hrm.Mapper.job;

import com.hrm.Entity.job.Assessment;
import com.hrm.dto.request.job.AssessmentRequest;
import com.hrm.dto.response.job.AssessmentResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class AssessmentMapperImpl implements AssessmentMapper {

    @Override
    public Assessment toAssessment(AssessmentRequest request) {
        if ( request == null ) {
            return null;
        }

        Assessment.AssessmentBuilder assessment = Assessment.builder();

        assessment.title( request.getTitle() );
        assessment.requirement( request.getRequirement() );

        return assessment.build();
    }

    @Override
    public AssessmentResponse toAssessmentResponse(Assessment assessment) {
        if ( assessment == null ) {
            return null;
        }

        AssessmentResponse.AssessmentResponseBuilder assessmentResponse = AssessmentResponse.builder();

        assessmentResponse.id( assessment.getId() );
        assessmentResponse.title( assessment.getTitle() );
        assessmentResponse.requirement( assessment.getRequirement() );

        return assessmentResponse.build();
    }

    @Override
    public void updateAssessment(Assessment assessment, AssessmentRequest request) {
        if ( request == null ) {
            return;
        }

        assessment.setTitle( request.getTitle() );
        assessment.setRequirement( request.getRequirement() );
    }
}
