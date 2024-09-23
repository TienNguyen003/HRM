package com.hrm.dto.request.timekeeping;

import jakarta.validation.constraints.Min;
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
public class TimeKeepingRequest {
    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "Định dạng ngày phải là HH:mm:ss")
    String time;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày phải là yyyy-MM-dd")
    String date;

    String reason;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;
}
