package com.hrm.dto.request.task;

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
public class TaskCreateRequest {
    @NotBlank(message = "Tên nhiệm vụ không được để trống")
    String title;

    String description;

    @Min(value = 1, message = "Độ ưu tiên phải lớn hơn 0")
    int priority;

    @NotNull(message = "Nhân viên nhận nhiệm vụ không được để trống")
    Integer employeeId;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày phải là yyyy-MM-dd")
    String dueDate;
}
