package com.hrm.dto.request.wage;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollRequest {
    String time;
    String advance;
    String totalSalary;
    int status = 0;
    int employeeId;
    int bankId;
}
