package com.hrm.dto.request.leave;

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
public class LeaveUpdateRequest {
    @NotNull(message = "Không được để trống")
    @Min(value = 0, message = "Trạng thái phải là số")
    int status;

    @NotBlank(message = "Tên người duyệt không được để trống")
    @Pattern(regexp = "^[\\p{L}]+(\\s[\\p{L}]+)*$", message = "Tên sở hữu chỉ được chứa chữ cái")
    String nameApproval;

    @NotNull(message = "Thời gian không được để trống")
    @Min(value = 0, message = "Thời gian phải lớn hơn 0")
    int time;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;
}
