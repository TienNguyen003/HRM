package com.hrm.dto.request.user.bank;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankUpdateRequest {
    @NotBlank(message = "Tên ngân hàng không được để trống")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Tên ngân hàng chỉ được chứa chữ cái")
    String nameBank;

    @NotBlank(message = "Tên sở hữu không được để trống")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Tên sở hữu chỉ được chứa chữ cái")
    String owner;

    @NotBlank(message = "Số tài khoản không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Số tài khoản chỉ được chứa số")
    String numberBank;

    @NotNull(message = "Độ ưu tiên không được để trống")
    @Min(value = 0, message = "Độ ưu tiên phải lớn hơn hoặc bằng 0")
    int priority;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;
}
