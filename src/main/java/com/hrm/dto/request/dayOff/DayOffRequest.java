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
public class DayOffRequest {
    String nameDay;
    int timeDay;
    LocalDateTime timeUpdate = LocalDateTime.now();
    int status = 1;
}