package com.hrm.dto.request.wage.salaryStaticValue;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WageUpdateRequest {
    @NotNull(message = "ID không được để trống")
    @Min(value = 1, message = "ID phải lớn hơn 0")
    int id;

    @NotBlank(message = "Không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Chỉ được chứa số")
    String salary;
}
