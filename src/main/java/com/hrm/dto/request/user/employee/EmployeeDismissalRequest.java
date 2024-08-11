package com.hrm.dto.request.user.employee;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDismissalRequest {
    String hire_date;
    String dismissal_date;
}
