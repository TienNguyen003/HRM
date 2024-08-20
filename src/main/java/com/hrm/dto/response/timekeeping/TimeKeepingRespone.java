package com.hrm.dto.response.timekeeping;

import com.hrm.Entity.user.Employee;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeKeepingRespone {
    int id;
    String time;
    String date;
    String reason;
    Employee employee;
}
