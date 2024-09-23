package com.hrm.dto.request.leave;

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
public class LeaveRequest {
    @NotNull(message = "Không được để trống")
    int dayOff;

    @NotBlank(message = "Không được để trống")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$", message = "Định dạng ngày phải là dd/MM/yyyy HH:mm")
    String startTime;

    @NotBlank(message = "Không được để trống")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$", message = "Định dạng ngày phải là dd/MM/yyyy HH:mm")
    String endTime;

    @NotBlank(message = "Không được để trống")
    String totalTime;

    String approved;

    String reason;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;

    int status = 0;

    String creationTime = String.valueOf(LocalDateTime.now());
}
