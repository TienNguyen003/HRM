package com.hrm.dto.response.user;

import com.hrm.Entity.user.Employee;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankRespone {
    int id;
    String nameBank;
    String owner;
    String numberBank;
    int priority;
    int status;
    Employee employee;
}
