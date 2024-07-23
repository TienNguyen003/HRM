package com.hrm.dto.response.user;

import com.hrm.Entity.office.Department;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

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
    String hire_date;
    String dismissal_date;
    int shift_id;
    int vacationTime;
    int hourOff;
    int vacationHours;
    Department department;
}
