package com.hrm.dto.response.wage;

import com.hrm.Entity.user.Employee;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WageMonthlyRespone {
    int id;
    String time;
    String wageCategories;
    String salary;
    Employee employee;
}
