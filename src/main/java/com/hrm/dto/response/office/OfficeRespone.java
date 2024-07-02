package com.hrm.dto.response.office;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfficeRespone {
    int id;
    String name;
    String address;
    String email;
    String phone;
    int status;
}
