package com.hrm.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeRequest {
    String name;
    String email;
    String phone_number;
    String gender;
    String image;
    String birth_date;
    String hire_date;
    int shift_id;
    int vacationTime;
    int hourOff;
    int vacationHours;
    int departmentId;
}
