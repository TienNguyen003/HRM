package com.hrm.dto.request.user.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserChangePassRequest {
    String id;
    String new_pass;
    String old_pass;
}
