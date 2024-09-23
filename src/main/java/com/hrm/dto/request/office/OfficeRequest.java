package com.hrm.dto.request.office;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfficeRequest {
    @NotBlank(message = "Tên không được để trống")
    String name;

    String address;
    String email;
    String phone;
}
