package com.hrm.dto.request.wage.advance;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdvanceRequest {
    LocalDateTime requestTime = LocalDateTime.now();

    @NotBlank(message = "Số tiền không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Số tiền chỉ được chứa số")
    String money;

    int status = 0;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;

    String note;
}
