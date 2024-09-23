package com.hrm.dto.request.dayOff;

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
public class SabbaticalLogsRequest {
    @NotBlank(message = "Nội dung không được để trống")
    String content;

    @NotNull(message = "Thời gian không được để trống")
    @Min(value = 0, message = "Thời gian phải lớn hơn 0")
    int fluctuatesTime;

    @NotNull(message = "Thời gian còn lại không được để trống")
    @Min(value = 0, message = "Thời gian còn lại phải lớn hơn 0")
    int remaining;

    LocalDateTime updateTime = LocalDateTime.now();

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;
}