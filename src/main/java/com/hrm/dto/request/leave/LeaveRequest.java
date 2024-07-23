package com.hrm.dto.request.leave;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequest {
    int dayOff;
    String startTime;
    String endTime;
    String totalTime;
    String approved;
    String reason;
    int employeeId;
    int status = 0;
    String creationTime = String.valueOf(LocalDateTime.now());
}
