package com.hrm.dto.response.wage;

import com.hrm.Entity.department.Department;
import com.hrm.Entity.user.Employee;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollRespone {
    String time;
    String salary;
    String advance;
    String totalSalary;
    int status = 0;
    String qrPay;
    Department department;
    Employee employee;
}
