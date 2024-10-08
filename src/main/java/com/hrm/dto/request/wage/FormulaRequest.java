package com.hrm.dto.request.wage;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormulaRequest {
    @NotBlank(message = "Không được để trống")
    String name;

    @NotBlank(message = "Không được để trống")
    String salaryFormula;
}
