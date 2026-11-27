package com.hrm.Entity.user;

import com.hrm.Entity.role.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;
	String username;
	String password;
	int status;
	boolean mustChangePassword = true;

	/** Tăng khi đổi quyền/password/trạng thái để thu hồi token đang lưu. */
	int tokenVersion;

	@OneToOne
	Employee employee;

	@ManyToOne
	Role role;
}
