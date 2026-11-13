package com.hrm.dto.response.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveReport {
    String title;
    long totalLeaves;
    long pendingLeaves;
    long approvedLeaves;
    long rejectedLeaves;
    double totalLeaveDays;
    String generatedAt;
}
