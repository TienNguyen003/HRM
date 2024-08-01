package com.hrm.dto.response.wage;

import com.hrm.Entity.user.Employee;
import com.hrm.Entity.wage.WageCategories;
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
    String salary;
    WageCategories wageCategories;
    Employee employee;
}
