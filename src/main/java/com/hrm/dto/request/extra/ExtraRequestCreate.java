package com.hrm.dto.request.extra;

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
public class ExtraRequestCreate {
    @NotNull(message = "Loại đơn không được để trống")
    @Min(value = 1, message = "Loại đơn không hợp lệ")
    int type;           // 1 tăng ca, 2 công tác, 3 từ xa

    @NotBlank(message = "Ngày không được để trống")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày phải là yyyy-MM-dd")
    String date;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày phải là yyyy-MM-dd")
    String endDate;     // nullable

    @Min(value = 0, message = "Số giờ không hợp lệ")
    double hours;

    String place;
    String reason;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(value = 1, message = "ID nhân viên phải lớn hơn 0")
    int employeeId;

    int status = 0;
}