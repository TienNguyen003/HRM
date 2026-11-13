package com.hrm.dto.response.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceReport {
    String title;
    long totalAttendanceTimes;
    int presentToday;
    long absentToday;
    String generatedAt;
}
