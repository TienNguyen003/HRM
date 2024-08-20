package com.hrm.Controller.user;

import com.hrm.Service.user.UserService;
import com.hrm.dto.request.user.user.UserChangePassRequest;
import com.hrm.dto.request.user.user.UserCreationRequest;
import com.hrm.dto.request.user.user.UserRsPass;
import com.hrm.dto.request.user.user.UserUpdateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.user.UserResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
	UserService userService;
	
	@PostMapping
	ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
		ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
		
		apiResponse.setResult(userService.createUser(request));
		
		return apiResponse;
	}
	
	@GetMapping
	ApiResponse<List<UserResponse>> getUsers(@RequestParam("pageNumber") int pageNumber ,
											 @RequestParam(name = "name", required = false) String name,
											 @RequestParam(name = "username", required = false) String username,
											 @RequestParam(name = "department", required = false) String department,
											 @RequestParam(name = "office", required = false) String office,
											 @RequestParam(name = "role", required = false) String role) {
		return ApiResponse.<List<UserResponse>>builder()
				.result(userService.getUsers(name, username, department, office, role, pageNumber, 30))
				.page(userService.getPagination(pageNumber, name, username, department, office, role))
				.build();
	}

	@GetMapping("/getAll")
	ApiResponse<List<UserResponse>> getUsers() {
		return ApiResponse.<List<UserResponse>>builder()
				.result(userService.getAll())
				.build();
	}

	@GetMapping("/user")
	ApiResponse<UserResponse> getUser(@RequestParam String userId) {
		return ApiResponse.<UserResponse>builder()
				.result(userService.getUser(userId))
				.build();
	}

	@GetMapping("/myInfo")
	ApiResponse<UserResponse> getInfo() {
		return ApiResponse.<UserResponse>builder()
				.result(userService.getInfo())
				.build();
	}

	@GetMapping("/update-pass")
	ApiResponse<String> updatePass(@RequestParam String userId, @RequestParam String new_pass) {
		return ApiResponse.<String>builder()
				.result(userService.updatePass(userId, new_pass))
				.build();
	}

	@PutMapping("/rs-pass")
	ApiResponse<String> resetPass( @RequestBody UserRsPass request) {
		return ApiResponse.<String>builder()
				.result(userService.rsPass(request))
				.build();
	}

	@PutMapping("/change-pass")
	ApiResponse<String> changePass( @RequestBody UserChangePassRequest request) {
		return ApiResponse.<String>builder()
				.result(userService.changePass(request))
				.build();
	}
	
	@PutMapping
	UserResponse updateUsser(@RequestParam String userId, @RequestBody UserUpdateRequest request) {
		return userService.updateUser(userId, request);
	}

	@DeleteMapping
	ApiResponse<String> deleteUser(@RequestParam String userId) {
		userService.deleteUser(userId);
		return ApiResponse.<String>builder()
				.result("User has been deleted")
				.build();
	}
}
