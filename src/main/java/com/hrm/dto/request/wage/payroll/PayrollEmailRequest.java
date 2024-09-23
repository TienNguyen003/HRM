package com.hrm.dto.request.wage.payroll;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollEmailRequest {
    @NotBlank(message = "Không được để trống")
    @Email(message = "Email không đúng định dạng")
    String email;

    @NotBlank(message = "Không được để trống")
    String content;
}
