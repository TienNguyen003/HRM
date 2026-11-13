package com.hrm.dto.response.dashboard;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardResponse {
    long totalEmployees;
    int presentToday;
    long pendingLeaves;
    long pendingAdvances;
    long pendingCvs;
    long totalDepartments;
}