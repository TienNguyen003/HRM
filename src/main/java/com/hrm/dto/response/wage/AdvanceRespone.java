package com.hrm.dto.response.wage;

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
public class AdvanceRespone {
    int id;
    LocalDateTime requestTime;
    LocalDateTime approvalTime;
    String money;
    String approvedBy;
    int status;
    Employee employee;
}
