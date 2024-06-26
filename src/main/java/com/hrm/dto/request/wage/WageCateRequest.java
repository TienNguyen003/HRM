package com.hrm.dto.request.wage;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WageCateRequest {
    String name;
    String symbol;
    String salaryType;
}
