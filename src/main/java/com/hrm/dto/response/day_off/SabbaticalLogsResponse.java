package com.hrm.dto.response.day_off;

import com.hrm.Entity.user.Employee;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SabbaticalLogsResponse {
    int id;
    String content;
    int fluctuatesTime;
    LocalDateTime timeUpdate;
    Employee employee;
}