package com.hrm.dto.response.user;

import com.hrm.Entity.user.Employee;
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
    String username;
    Employee employee;
    RoleResponse role;
}
