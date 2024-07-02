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
    Date birth_date;
    Date hire_date;
    int shift_id;
    int vacationTime;
    String department;
}
