package com.hrm.dto.response.day_off;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRespone {
    int id;
    String name;
    String dayOff;
    String startTime;
    String endTime;
    String totalTime;
    String approved;
    String reason;
    int employeeId;
    int status;
    LocalDateTime creationTime;
}
