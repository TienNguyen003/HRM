package com.hrm.dto.request.office;

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
public class DepartmentRequest {
    @NotBlank(message = "Tên không được để trống")
    String name;

    @NotBlank(message = "Tên ngắn không được để trống")
    String shortName;

    @NotNull(message = "Công ty không được để trống")
    @Min(value = 1, message = "Công ty phải lớn hơn 0")
    int officeId;
}
