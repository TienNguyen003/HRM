package com.hrm.dto.request.job;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DisqualifiedUpdateRequest {
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 2, message = "Trạng thái không hợp lệ")
    int status;
}