package com.hrm.dto.request.wage.salaryDynamicValues;

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
public class WageMonthlyRequest {
    @NotBlank(message = "Thời gian không được để trống")
    @Pattern(regexp = "^\\d{2}/\\d{4}$", message = "Định dạng ngày phải là MM/yyyy")
    String time;

    @NotNull(message = "ID danh mục không được để trống")
    @Min(value = 1, message = "ID danh mục phải lớn hơn 0")
    int wageCategoriesId;

    @NotBlank(message = "Không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Chỉ được chứa số")
    String salary;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;
}
