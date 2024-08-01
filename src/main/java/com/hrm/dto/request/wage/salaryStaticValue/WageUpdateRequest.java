package com.hrm.dto.request.wage.salaryStaticValue;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WageUpdateRequest {
    int id;
    String salary;
}
