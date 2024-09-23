package com.hrm.dto.request.user.contract;

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
public class ContractsUpdateRequest {
    String urlFile;
    String linkFile;

    @NotBlank(message = "Ngày bắt đầu không được để trống")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày bắt đầu phải là yyyy-MM-dd")
    String hire_date;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày kết thúc phải là yyyy-MM-dd")
    String dismissal_date;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;
}
