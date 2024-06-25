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
public class HolidayRequest {
    String name;
    String startTime;
    String endTime;
    String totalTime;
}