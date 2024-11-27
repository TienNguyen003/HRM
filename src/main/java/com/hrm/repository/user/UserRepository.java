package com.hrm.repository.user;

import com.hrm.Entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
	boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);

	@Query("SELECT u FROM User u WHERE u.status = 1")
	List<User> findAllUserActive();

	@Query("SELECT u FROM User u WHERE" +
			"(:username IS NULL OR u.username LIKE %:username%) AND" +
			"(:department IS NULL OR u.employee.department.name LIKE %:department%) AND" +
			"(:office IS NULL OR u.employee.department.officeI.name LIKE %:office%) AND" +
			"(:role IS NULL OR u.role.name LIKE %:role%) AND" +
			"(:name IS NULL OR u.employee.name LIKE %:name%)")
	Page<User> findByName
			(String name, String username, String department, String office, String role, Pageable pageable);
}
