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
public class RequirementsRequest {
    @NotBlank(message = "Tên không được để trống")
    String title;

    @Min(value = 0, message = "Trường pha lớn hơn 0")
    int assessmentId;

    /** Từ khóa tìm kiếm trong CV; nếu trống sẽ dùng title. */
    String keyword;

    /** Điểm tối đa / trọng số, mặc định 1. */
    @Min(value = 1, message = "Trọng số phải lớn hơn 0")
    int weight = 1;

    /** Tiêu chí bắt buộc: thiếu thì CV bị loại. */
    boolean mandatory;
}
