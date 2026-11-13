package com.hrm.Mapper.job;

import com.hrm.Entity.job.JobPosting;
import com.hrm.dto.request.job.JobPostingRequest;
import com.hrm.dto.response.job.JobPostingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface JobPostingMapper {
    JobPosting toJobPosting(JobPostingRequest request);

    JobPostingResponse toJobPostingResponse(JobPosting jobPosting);

    void updateJobPosting(@MappingTarget JobPosting jobPosting, JobPostingRequest request);
}
