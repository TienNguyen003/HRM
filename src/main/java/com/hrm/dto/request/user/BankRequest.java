package com.hrm.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankRequest {
    String nameBank;
    String owner;
    String numberBank;
    int priority;
    int status= 1 ;
    int employeeId;
}
