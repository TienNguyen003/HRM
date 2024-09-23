package com.hrm.dto.request.wage.advance;

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
public class AdvanceUpdateSttRequest {
    LocalDateTime approvalTime = LocalDateTime.now();

    @NotBlank(message = "Người duyệt không được để trống")
    String approvedBy;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái phải lớn hơn 0")
    int status;
}
