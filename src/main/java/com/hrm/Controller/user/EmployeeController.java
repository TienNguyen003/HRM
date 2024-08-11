package com.hrm.Controller.user;

import com.hrm.Service.user.EmployeeService;
import com.hrm.dto.request.user.employee.EmployeeDismissalRequest;
import com.hrm.dto.request.user.employee.EmployeeRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.user.EmployeeRespone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}employee")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmployeeController {
	EmployeeService employeeService;
	
	@PostMapping
	ApiResponse<EmployeeRespone> create(@RequestBody EmployeeRequest request, @RequestParam String username) {
		return ApiResponse.<EmployeeRespone>builder()
				.result(employeeService.createB(request, username))
				.build();
	}
	
	@GetMapping
	ApiResponse<List<EmployeeRespone>> getAll(@RequestParam("pageNumber") int pageNumber) {
		return ApiResponse.<List<EmployeeRespone>>builder()
				.result(employeeService.getAllB(pageNumber, 30))
				.build();
	}

	@GetMapping("/employee")
	ApiResponse<EmployeeRespone> getById(@RequestParam("id") int id) {
		return ApiResponse.<EmployeeRespone>builder()
				.result(employeeService.getById(id))
				.build();
	}
	
	@PutMapping
	ApiResponse<EmployeeRespone> update(@RequestParam int id, @RequestBody EmployeeRequest request) {
		return ApiResponse.<EmployeeRespone>builder()
				.result(employeeService.updateB(id, request))
				.build();
	}

	@PutMapping("/dismissal")
	ApiResponse<EmployeeRespone> updateDismissal(@RequestParam int id, @RequestBody EmployeeDismissalRequest request) {
		return ApiResponse.<EmployeeRespone>builder()
				.result(employeeService.updateDismissal(id, request))
				.build();
	}

	@DeleteMapping
	ApiResponse<String> delete (@RequestParam int id){
		employeeService.deleteB(id);
		return ApiResponse.<String>builder()
				.result("This employee has been deleted")
				.build();
	}
}
