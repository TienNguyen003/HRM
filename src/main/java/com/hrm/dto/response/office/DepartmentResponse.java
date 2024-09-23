package com.hrm.dto.response.office;

import com.hrm.Entity.office.OfficeI;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentResponse {
    String id;
    String name;
    String shortName;
    int status;
    OfficeI officeI;
}
