package com.hrm.dto.response.user;

import com.hrm.Entity.user.Employee;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractsRespone {
    String id;
    String urlFile;
    String linkFile;
    String hire_date;
    String dismissal_date;
    Employee employee;
    int status;
}
