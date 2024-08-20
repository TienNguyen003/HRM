package com.hrm.dto.response.wage;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormulaResponse {
    int id;
    String name;
    String salaryFormula;
    int status;
}
