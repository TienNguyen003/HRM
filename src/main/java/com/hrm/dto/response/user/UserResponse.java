package com.hrm.dto.response.user;

import com.hrm.dto.response.role.RoleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse  {
    String id;
    int employeeId;
    String username;
    Set<RoleResponse> role;
}
