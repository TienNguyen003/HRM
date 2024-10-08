package com.hrm.dto.request.dayOff.dayoff;

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
public class DayOffUpdateRequest {
    @NotBlank(message = "Tên ngày nghỉ không được để trống")
    String nameDay;

    @NotNull(message = "Thời gian không được để trống")
    @Min(value = 0, message = "Thời gian phải lớn hơn 0")
    int timeDay;

    LocalDateTime timeUpdate = LocalDateTime.now();
}