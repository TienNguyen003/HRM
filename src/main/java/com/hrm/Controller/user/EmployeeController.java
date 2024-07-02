package com.hrm.Controller.user;

import com.hrm.Service.user.EmployeeService;
import com.hrm.dto.request.user.EmployeeRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.user.EmployeeRespone;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}bank_accounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmployeeController {
	EmployeeService employeeService;
	
	@PostMapping
	ApiResponse<EmployeeRespone> create(@RequestBody @Valid EmployeeRequest request) {
		return ApiResponse.<EmployeeRespone>builder()
				.result(employeeService.createB(request))
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

//	@GetMapping("/search")
//	ApiResponse<List<BankRespone>> search(@RequestParam("pageNumber") int pageNumber ,
//										  @RequestParam(name = "name", required = false) String name,
//										  @RequestParam(name = "status", required = false) Integer status,
//										  @RequestParam(name = "priority", required = false) Integer priority,
//										  @RequestParam(name = "nameBank", required = false) String nameBank){
//		return ApiResponse.<List<BankRespone>>builder()
//				.result(bankService.searchAllB(pageNumber, 30, name, nameBank, status, priority))
//				.build();
//	}
	
	@PutMapping
	ApiResponse<EmployeeRespone> update(@RequestParam int id, @RequestBody EmployeeRequest request) {
		return ApiResponse.<EmployeeRespone>builder()
				.result(employeeService.updateB(id, request))
				.build();
	}

	ApiResponse<String> delete (@RequestParam int id){
		employeeService.deleteB(id);
		return ApiResponse.<String>builder()
				.result("This employee has been deleted")
				.build();
	}
}
