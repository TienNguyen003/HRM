package com.hrm.dto.request.dayOff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HolidayRequest {
    @NotBlank(message = "Tên ngày nghỉ không được để trống")
    String name;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày phải là yyyy-MM-dd")
    String startTime;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày phải là yyyy-MM-dd")
    String endTime;

    @NotBlank(message = "Tổng thời gian không được để trống")
    @Pattern(regexp = "^[0-9]+$", message = "Tổng thời gian chỉ được chứa số")
    String totalTime;
}