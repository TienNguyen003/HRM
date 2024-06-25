package com.hrm.dto.request.wage;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdvanceUpdateSttRequest {
    LocalDateTime approvalTime = LocalDateTime.now();;
    String approvedBy;
    int status;
}
