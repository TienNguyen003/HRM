package com.hrm.Mapper.job;

import com.hrm.Entity.job.Assessment;
import com.hrm.dto.request.job.AssessmentRequest;
import com.hrm.dto.response.job.AssessmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AssessmentMapper {
    Assessment toAssessment(AssessmentRequest request);

    AssessmentResponse toAssessmentResponse(Assessment assessment);

    void updateAssessment(@MappingTarget Assessment assessment, AssessmentRequest request);
}
