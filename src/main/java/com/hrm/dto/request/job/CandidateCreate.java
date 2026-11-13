package com.hrm.dto.request.job;

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
public class CandidateCreate {
    @NotBlank(message = "Email không được để trống")
    String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    String phone;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Định dạng ngày phải là yyyy-MM-dd")
    String dob;

    @NotNull(message = "Tin tuyển dụng không được để trống")
    Integer jobId;
}