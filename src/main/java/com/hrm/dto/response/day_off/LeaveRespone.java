package com.hrm.dto.response.day_off;

import com.hrm.Entity.day_off.DayOffCategories;
import com.hrm.Entity.user.Employee;
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
    String startTime;
    String endTime;
    String totalTime;
    String approved;
    String reason;
    int status;
    String creationTime;

    DayOffCategories dayOffCategories;
    Employee employee;
}
