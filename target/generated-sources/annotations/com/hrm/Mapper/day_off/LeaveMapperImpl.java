package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.ApplicationLeave;
import com.hrm.dto.request.leave.LeaveRequest;
import com.hrm.dto.response.day_off.LeaveRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class LeaveMapperImpl implements LeaveMapper {

    @Override
    public ApplicationLeave toLeave(LeaveRequest leaveRequest) {
        if ( leaveRequest == null ) {
            return null;
        }

        ApplicationLeave.ApplicationLeaveBuilder applicationLeave = ApplicationLeave.builder();

        applicationLeave.approved( leaveRequest.getApproved() );
        applicationLeave.creationTime( leaveRequest.getCreationTime() );
        applicationLeave.endTime( leaveRequest.getEndTime() );
        applicationLeave.reason( leaveRequest.getReason() );
        applicationLeave.startTime( leaveRequest.getStartTime() );
        applicationLeave.status( leaveRequest.getStatus() );
        applicationLeave.totalTime( leaveRequest.getTotalTime() );

        return applicationLeave.build();
    }

    @Override
    public LeaveRespone toLeaveResponse(ApplicationLeave applicationLeave) {
        if ( applicationLeave == null ) {
            return null;
        }

        LeaveRespone.LeaveResponeBuilder leaveRespone = LeaveRespone.builder();

        leaveRespone.approved( applicationLeave.getApproved() );
        leaveRespone.creationTime( applicationLeave.getCreationTime() );
        leaveRespone.dayOffCategories( applicationLeave.getDayOffCategories() );
        leaveRespone.employee( applicationLeave.getEmployee() );
        leaveRespone.endTime( applicationLeave.getEndTime() );
        leaveRespone.id( applicationLeave.getId() );
        leaveRespone.reason( applicationLeave.getReason() );
        leaveRespone.startTime( applicationLeave.getStartTime() );
        leaveRespone.status( applicationLeave.getStatus() );
        leaveRespone.totalTime( applicationLeave.getTotalTime() );

        return leaveRespone.build();
    }

    @Override
    public void updateLeaveRq(ApplicationLeave applicationLeave, LeaveRequest request) {
        if ( request == null ) {
            return;
        }

        applicationLeave.setApproved( request.getApproved() );
        applicationLeave.setCreationTime( request.getCreationTime() );
        applicationLeave.setEndTime( request.getEndTime() );
        applicationLeave.setReason( request.getReason() );
        applicationLeave.setStartTime( request.getStartTime() );
        applicationLeave.setStatus( request.getStatus() );
        applicationLeave.setTotalTime( request.getTotalTime() );
    }
}
