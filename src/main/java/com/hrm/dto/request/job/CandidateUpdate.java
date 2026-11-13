package com.hrm.dto.request.job;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateUpdate {
    // giai đoạn pipeline: 0 mới, 1 đã sàng lọc, 2 phỏng vấn, 3 tuyển dụng, 4 loại
    @NotNull(message = "Giai đoạn không được để trống")
    @Min(value = 0, message = "Giai đoạn không hợp lệ")
    @Max(value = 4, message = "Giai đoạn không hợp lệ")
    int stage;

    String interviewTime;   // yyyy-MM-dd HH:mm (nullable)
}