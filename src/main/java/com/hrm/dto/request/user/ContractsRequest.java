package com.hrm.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractsRequest {
    String urlFile;
    int status = 1;
    int employeeId;
}
