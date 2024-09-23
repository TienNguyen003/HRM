package com.hrm.dto.request.wage.payroll;

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
public class PayrollRequest {
    @NotBlank(message = "Thời gian không được để trống")
    @Pattern(regexp = "^(0[1-9]|1[0-2]|[1-9])/\\d{4}$", message = "Định dạng ngày phải là MM/yyyy")
    String time;

    @NotBlank(message = "Không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Chỉ được chứa số")
    String advance;

    @NotBlank(message = "Không được để trống")
    @Pattern(regexp = "^-?[0-9]+$", message = "Chỉ được chứa số")
    String totalSalary;

    int status = 0;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;

    @NotNull(message = "ID ngân hàng không được để trống")
    @Min(value = 1, message = "ID ngân hàng phải lớn hơn 0")
    int bankId;
}
