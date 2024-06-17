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
    @NotNull(message = "Tên không được để trống")
    String name;

    @NotNull(message = "Vui lòng chọn")
    String dayOff;
    String startTime;
    String endTime;
    String totalTime;
    String approved;
    String reason;
    int employeeId;
    int status = 0;
    LocalDateTime creationTime = LocalDateTime.now();
}
