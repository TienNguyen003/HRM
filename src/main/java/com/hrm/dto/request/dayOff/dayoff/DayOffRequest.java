package com.hrm.dto.request.dayOff.dayoff;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DayOffRequest {
    @NotBlank(message = "Tên ngày nghỉ không được để trống")
    String nameDay;

    @NotNull(message = "Thời gian không được để trống")
    @Min(value = 0, message = "Thời gian phải lớn hơn 0")
    int timeDay;

    LocalDateTime timeUpdate = LocalDateTime.now();

    int status = 1;
}