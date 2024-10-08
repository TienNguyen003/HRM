package com.hrm.dto.response.user;

import com.hrm.Entity.office.Department;
import com.hrm.Entity.wage.Formula;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeRespone {
    int id;
    String name;
    String email;
    String phone_number;
    String gender;
    String image;
    String birth_date;
    String joined_date;
    int shift_id;
    int vacationTime;
    int hourOff;
    int vacationHours;
    int timekeeping;
    Department department;
    Formula formula;
}
