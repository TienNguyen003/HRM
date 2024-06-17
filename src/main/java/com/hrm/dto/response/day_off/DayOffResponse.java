package com.hrm.dto.response.day_off;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DayOffResponse {
    int id;
    String nameDay;
    int timeDay;
    LocalDateTime timeUpdate;
    int status;
}
