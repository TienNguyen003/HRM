package com.hrm.dto.response.office;

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
    String belongTo;
    int status;
}
