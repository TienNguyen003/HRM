package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.ApplicationLeave;
import com.hrm.dto.request.leave.LeaveRequest;
import com.hrm.dto.response.day_off.LeaveRespone;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class LeaveMapperImpl implements LeaveMapper {

    @Override
    public ApplicationLeave toLeave(LeaveRequest leaveRequest) {
        if ( leaveRequest == null ) {
            return null;
        }

        ApplicationLeave.ApplicationLeaveBuilder applicationLeave = ApplicationLeave.builder();

        applicationLeave.startTime( leaveRequest.getStartTime() );
        applicationLeave.endTime( leaveRequest.getEndTime() );
        applicationLeave.totalTime( leaveRequest.getTotalTime() );
        applicationLeave.approved( leaveRequest.getApproved() );
        applicationLeave.reason( leaveRequest.getReason() );
        applicationLeave.status( leaveRequest.getStatus() );
        applicationLeave.creationTime( leaveRequest.getCreationTime() );

        return applicationLeave.build();
    }

    @Override
    public LeaveRespone toLeaveResponse(ApplicationLeave applicationLeave) {
        if ( applicationLeave == null ) {
            return null;
        }

        LeaveRespone.LeaveResponeBuilder leaveRespone = LeaveRespone.builder();

        leaveRespone.id( applicationLeave.getId() );
        leaveRespone.startTime( applicationLeave.getStartTime() );
        leaveRespone.endTime( applicationLeave.getEndTime() );
        leaveRespone.totalTime( applicationLeave.getTotalTime() );
        leaveRespone.approved( applicationLeave.getApproved() );
        leaveRespone.reason( applicationLeave.getReason() );
        leaveRespone.status( applicationLeave.getStatus() );
        leaveRespone.creationTime( applicationLeave.getCreationTime() );
        leaveRespone.dayOffCategories( applicationLeave.getDayOffCategories() );
        leaveRespone.employee( applicationLeave.getEmployee() );

        return leaveRespone.build();
    }

    @Override
    public void updateLeaveRq(ApplicationLeave applicationLeave, LeaveRequest request) {
        if ( request == null ) {
            return;
        }

        applicationLeave.setStartTime( request.getStartTime() );
        applicationLeave.setEndTime( request.getEndTime() );
        applicationLeave.setTotalTime( request.getTotalTime() );
        applicationLeave.setApproved( request.getApproved() );
        applicationLeave.setReason( request.getReason() );
        applicationLeave.setStatus( request.getStatus() );
        applicationLeave.setCreationTime( request.getCreationTime() );
    }
}
