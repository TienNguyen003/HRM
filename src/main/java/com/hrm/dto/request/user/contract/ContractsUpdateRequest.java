package com.hrm.dto.request.user.contract;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractsUpdateRequest {
    String urlFile;
    int employeeId;
}
