package com.hrm.Service.user;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.role.Role;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.user.User;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.user.UserMapper;
import com.hrm.dto.request.user.UserCreationRequest;
import com.hrm.dto.request.user.UserUpdateRequest;
import com.hrm.dto.response.user.UserResponse;
import com.hrm.repository.role.RoleRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
	UserRepository userRepository;
	RoleRepository roleRepository;
	EmployeeRepository employeeRepository;
	UserMapper userMapper;
	PasswordEncoder passwordEncoder;
	
	public UserResponse createUser(UserCreationRequest request) {
		if(userRepository.existsByUsername(request.getUsername()))
			throw new AppException(ErrorCode.USER_EXISTED);

		Employee employee = employeeRepository.findById(request.getEmployeeId())
				.orElseThrow(() -> new RuntimeException("No employee not found"));
		com.hrm.Entity.role.Role role = roleRepository.findById(request.getRoleName())
				.orElseThrow(() -> new RuntimeException("No role not found"));
		User user = userMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		user.setRole(role);
		user.setEmployee(employee);
		
		return userMapper.toUserResponse(userRepository.save(user));
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> getUsers(String name, String username, String department, String role, int pageNumber, int pageSize){
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		return userRepository.findByName(name, username, department, role, pageable)
				.stream().map(userMapper::toUserResponse).toList();
	}

	public PageCustom getPagination(int pageNumber, long count){
		String totalItems = String.valueOf(roleRepository.totalItems());
		String totalPages = String.valueOf(roleRepository.totalPages());
		return PageCustom.builder()
				.totalPages(totalPages)
				.totalItems(totalItems)
				.totalItemsPerPage(String.valueOf(count))
				.currentPage(String.valueOf(pageNumber))
				.build();
	}

	public UserResponse getUser(String id) {
		return userMapper.toUserResponse(userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found")));
	}
	
	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		userMapper.updateUser(user, request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		com.hrm.Entity.role.Role role = roleRepository.findById(request.getRole())
				.orElseThrow(() -> new RuntimeException("No role not found"));
		user.setRole(role);
		
		return userMapper.toUserResponse(userRepository.save(user));
	}
	
	public void deleteUser(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("No user not found"));
		userRepository.deleteById(id);
		employeeRepository.deleteById(user.getEmployee().getId());
	}

	public UserResponse getInfo(){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		return userMapper.toUserResponse(user);
	}
}
