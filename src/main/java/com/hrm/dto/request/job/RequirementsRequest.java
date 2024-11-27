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
}
