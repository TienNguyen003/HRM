package com.hrm.dto.request.leave;

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
public class LeaveUpdateRequest {
    @NotNull(message = "Vui lòng chọn")
    String dayOff;
    String startTime;
    String endTime;
    String totalTime;
    String approved;
    String reason;
    int employeeId;
    int status = 1;
    LocalDateTime creationTime = LocalDateTime.now();
}
