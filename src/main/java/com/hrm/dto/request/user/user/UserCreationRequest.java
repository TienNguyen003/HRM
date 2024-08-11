package com.hrm.dto.request.user.user;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
	@Size(min = 6, message = "USERNAME_INVALID")
	String username;
	
	@Size(min = 8, message = "PASSWORD_INVALID")
	String password;

	int employeeId;
	String roleName;
}
