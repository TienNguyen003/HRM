package com.hrm.dto.request.office;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfficeRequest {
    String name;
    String address;
    String email;
    String phone;
    int status;
}
