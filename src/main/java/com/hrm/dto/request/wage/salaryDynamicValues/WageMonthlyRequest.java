package com.hrm.dto.request.wage.salaryDynamicValues;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WageMonthlyRequest {
    String time;
    int wageCategoriesId;
    String salary;
    int employeeId;
}
