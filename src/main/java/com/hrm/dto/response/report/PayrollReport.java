package com.hrm.dto.response.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollReport {
    String title;
    long totalEmployees;
    double totalPayroll;
    double averageSalary;
    String generatedAt;
}
