package com.hrm.dto.request.wage;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WageRequest {
    String time;
    String wageCategories;
    String salary;
    int employeeId;
}
