package com.hrm.Mapper.day_off;

import com.hrm.Entity.day_off.ApplicationLeave;
import com.hrm.dto.request.leave.LeaveRequest;
import com.hrm.dto.request.leave.LeaveUpdateRequest;
import com.hrm.dto.response.day_off.LeaveRespone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LeaveMapper {
    ApplicationLeave toLeave(LeaveRequest leaveRequest);

    LeaveRespone toLeaveResponse(ApplicationLeave applicationLeave);

    void updateLeaveRq(@MappingTarget ApplicationLeave applicationLeave, LeaveRequest request);
}