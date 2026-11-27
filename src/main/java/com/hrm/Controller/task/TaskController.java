package com.hrm.Controller.task;

import com.hrm.Service.task.TaskService;
import com.hrm.dto.request.task.TaskCreateRequest;
import com.hrm.dto.request.task.TaskUpdateRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.task.TaskResponse;
import com.hrm.enums.TaskStatus;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}tasks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {
    TaskService taskService;

    @PreAuthorize("@requiredPermission.checkPermission('TASK_MANAGE')")
    @PostMapping
    ApiResponse<TaskResponse> create(@RequestBody @Valid TaskCreateRequest request) {
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.create(request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('TASK_MANAGE')")
    @PutMapping
    ApiResponse<TaskResponse> update(@RequestParam int id,
                                     @RequestBody @Valid TaskUpdateRequest request) {
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.update(id, request))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('TASK_VIEW')")
    @GetMapping("/detail")
    ApiResponse<TaskResponse> getDetail(@RequestParam int id) {
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.getById(id))
                .build();
    }

    // /me luôn trả nhiệm vụ của chính người đăng nhập.
    @GetMapping("/me")
    ApiResponse<List<TaskResponse>> getMyTasks() {
        return ApiResponse.<List<TaskResponse>>builder()
                .result(taskService.getMyTasks())
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('TASK_VIEW')")
    @GetMapping
    ApiResponse<List<TaskResponse>> search(@RequestParam("pageNumber") int pageNumber,
                                           @RequestParam(name = "pageSize", defaultValue = "30") int pageSize,
                                           @RequestParam(name = "title", required = false) String title,
                                           @RequestParam(name = "status", required = false) TaskStatus status,
                                           @RequestParam(name = "employeeId", required = false) Integer employeeId) {
        return ApiResponse.<List<TaskResponse>>builder()
                .result(taskService.search(pageNumber, pageSize, title, status, employeeId))
                .page(taskService.getPagination(pageNumber, pageSize, title, status, employeeId))
                .build();
    }

    @PreAuthorize("@requiredPermission.checkPermission('TASK_MANAGE')")
    @DeleteMapping
    ApiResponse<String> delete(@RequestParam int id) {
        taskService.delete(id);
        return ApiResponse.<String>builder()
                .result("Nhiệm vụ đã được xóa")
                .build();
    }
}
