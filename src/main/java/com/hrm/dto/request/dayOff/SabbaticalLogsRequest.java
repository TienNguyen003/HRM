package com.hrm.dto.request.dayOff;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SabbaticalLogsRequest {
    String content;
    int fluctuatesTime;
    int remaining;
    LocalDateTime updateTime = LocalDateTime.now();
    int employeeId;
}