package com.hrm.dto.request.job;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobPostingRequest {
    @NotBlank(message = "Chức danh không được để trống")
    String title;

    String description;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    int quantity;

    String salaryRange;
    String requirements;

    int status = 0;
}