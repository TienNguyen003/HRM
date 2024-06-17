package com.hrm.Service.user;

import com.hrm.Entity.user.User;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.user.UserMapper;
import com.hrm.dto.request.user.UserCreationRequest;
import com.hrm.dto.request.user.UserUpdateRequest;
import com.hrm.dto.response.user.UserResponse;
import com.hrm.enums.Role;
import com.hrm.repository.user.RoleRepository;
import com.hrm.repository.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
	UserRepository userRepository;
	RoleRepository roleRepository;
	UserMapper userMapper;
	PasswordEncoder passwordEncoder;
	
	public UserResponse createUser(UserCreationRequest request) {
		if(userRepository.existsByUsername(request.getUsername()))
			throw new AppException(ErrorCode.USER_EXISTED);
		
		User user = userMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		HashSet<Role> roles = new HashSet<>();
//		roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

		//user.setRole(roles);
		
		return userMapper.toUserResponse(userRepository.save(user));
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> getUsers(){
		return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
	}

	@PostAuthorize("returnObject.username == authentication.name")
	public UserResponse getUser(String id) {
		return userMapper.toUserResponse(userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found")));
	}
	
	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		userMapper.updateUser(user, request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		var roles = roleRepository.findAllById(request.getRole());
		user.setRole(new HashSet<>(roles));
		
		return userMapper.toUserResponse(userRepository.save(user));
	}
	
	public void deleteUser(String id) {
		userRepository.deleteById(id);
	}

	public UserResponse getInfo(){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		return userMapper.toUserResponse(user);
	}
}
