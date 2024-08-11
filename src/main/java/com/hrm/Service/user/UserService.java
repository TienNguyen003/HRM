package com.hrm.Service.user;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.user.User;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.user.UserMapper;
import com.hrm.Service.EmailService;
import com.hrm.dto.request.user.user.UserCreationRequest;
import com.hrm.dto.request.user.user.UserRsPass;
import com.hrm.dto.request.user.user.UserUpdateRequest;
import com.hrm.dto.response.user.UserResponse;
import com.hrm.repository.role.RoleRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
	EmailService emailService;
	UserRepository userRepository;
	RoleRepository roleRepository;
	EmployeeRepository employeeRepository;
	UserMapper userMapper;
	PasswordEncoder passwordEncoder;
	
	public UserResponse createUser(UserCreationRequest request) {
		if(userRepository.existsByUsername(request.getUsername()))
			throw new AppException(ErrorCode.USERNAME_EXISTED);

		Employee employee = employeeRepository.findById(request.getEmployeeId())
				.orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));
		com.hrm.Entity.role.Role role = roleRepository.findById(request.getRoleName())
				.orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
		User user = userMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		user.setRole(role);
		user.setEmployee(employee);
		
		return userMapper.toUserResponse(userRepository.save(user));
	}

	public List<UserResponse> getAll(){
		return userRepository.findAll()
				.stream().map(userMapper::toUserResponse).toList();
	}

	public List<UserResponse> getUsers(String name, String username, String department, String office, String role, int pageNumber, int pageSize){
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		return userRepository.findByName(name, username, department, office, role, pageable)
				.stream().map(userMapper::toUserResponse).toList();
	}

	public PageCustom getPagination(int pageNumber, String name, String username, String department, String office, String role){
		Pageable pageable = PageRequest.of(pageNumber - 1, 30);
		Page<User> page = userRepository.findByName(name, username, department, office, role, pageable);
		return PageCustom.builder()
				.totalPages(String.valueOf(page.getTotalPages()))
				.totalItems(String.valueOf(page.getTotalElements()))
				.totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
				.currentPage(String.valueOf(pageNumber))
				.build();
	}

	public UserResponse getUser(String id) {
		return userMapper.toUserResponse(userRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED)));
	}
	
	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));

		userMapper.updateUser(user, request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		com.hrm.Entity.role.Role role = roleRepository.findById(request.getRole())
				.orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));
		user.setRole(role);
		
		return userMapper.toUserResponse(userRepository.save(user));
	}
	
	public void deleteUser(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));
		userRepository.deleteById(id);
		employeeRepository.deleteById(user.getEmployee().getId());
	}

	public UserResponse getInfo(){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));

		return userMapper.toUserResponse(user);
	}

	public String rsPass(UserRsPass request) {
		User user = userRepository.findById(request.getId())
				.orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));

		emailService.requestPasswordReset(user.getId(), request.getEmail(), request.getNew_pass());

		return "Please check your email";
	}

	public String updatePass(String userId, String new_pass) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_EXISTED));

		user.setPassword(passwordEncoder.encode(new_pass));
		userRepository.save(user);

		return "Update success";
	}
}
