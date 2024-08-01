package com.hrm.dto.request.wage.advance;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdvanceRequest {
    LocalDateTime requestTime = LocalDateTime.now();
    String money;
    int status = 0;
    int employeeId;
    String note;
}
