package com.hrm.dto.request.timekeeping;

import com.hrm.Entity.user.Employee;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeKeepingRequest {
    String time;
    String date;
    String reason;
    int employeeId;
}
