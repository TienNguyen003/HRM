package com.hrm.dto.response.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SummaryReport {
    String title;
    String generatedAt;
    Map<String, Object> statistics;
}
