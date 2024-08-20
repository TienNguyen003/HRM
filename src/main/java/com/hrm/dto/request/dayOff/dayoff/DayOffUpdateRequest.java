package com.hrm.dto.request.dayOff.dayoff;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DayOffUpdateRequest {
    String nameDay;
    int timeDay;
    LocalDateTime timeUpdate = LocalDateTime.now();
}