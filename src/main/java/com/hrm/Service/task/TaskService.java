package com.hrm.Service.task;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.task.Task;
import com.hrm.Entity.user.Employee;
import com.hrm.Entity.user.User;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.dto.request.task.TaskCreateRequest;
import com.hrm.dto.request.task.TaskUpdateRequest;
import com.hrm.dto.response.task.TaskResponse;
import com.hrm.enums.TaskStatus;
import com.hrm.repository.task.TaskRepository;
import com.hrm.repository.user.EmployeeRepository;
import com.hrm.repository.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskService {
    TaskRepository taskRepository;
    EmployeeRepository employeeRepository;
    UserRepository userRepository;

    // giao nhiệm vụ mới
    public TaskResponse create(TaskCreateRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TaskStatus.PENDING)
                .creationTime(String.valueOf(LocalDateTime.now()))
                .dueDate(request.getDueDate())
                .employee(employee)
                .build();

        return toResponse(taskRepository.save(task));
    }

    // cập nhật nhiệm vụ
    public TaskResponse update(int id, TaskUpdateRequest request) {
        Task task = getTask(id);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());

        return toResponse(taskRepository.save(task));
    }

    // lấy theo id
    public TaskResponse getById(int id) {
        return toResponse(getTask(id));
    }

    // tìm kiếm theo tên / trạng thái / nhân viên
    public List<TaskResponse> search(int pageNumber, int pageSize,
                                     String title, TaskStatus status, Integer employeeId) {
        return searchPage(pageNumber, pageSize, title, status, employeeId)
                .stream().map(this::toResponse).toList();
    }

    // thông tin phân trang
    public PageCustom getPagination(int pageNumber, int pageSize,
                                    String title, TaskStatus status, Integer employeeId) {
        Page<Task> page = searchPage(pageNumber, pageSize, title, status, employeeId);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // nhiệm vụ của tài khoản đang đăng nhập
    public List<TaskResponse> getMyTasks() {
        User user = userRepository.findByUsername(currentUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (user.getEmployee() == null) return List.of();

        return taskRepository.findAllByEmployeeId(user.getEmployee().getId())
                .stream().map(this::toResponse).toList();
    }

    // xóa nhiệm vụ
    public void delete(int id) {
        taskRepository.delete(getTask(id));
    }

    // số nhiệm vụ theo trạng thái (dùng cho dashboard)
    public long countByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }

    private Page<Task> searchPage(int pageNumber, int pageSize,
                                  String title, TaskStatus status, Integer employeeId) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        if (employeeId != null && status != null)
            return taskRepository.findByEmployeeIdAndStatus(employeeId, status, pageable);
        if (employeeId != null)
            return taskRepository.findByEmployeeId(employeeId, pageable);
        if (status != null)
            return taskRepository.findByStatus(status, pageable);
        if (title != null && !title.isBlank())
            return taskRepository.findByTitleContaining(title, pageable);
        return taskRepository.findAll(pageable);
    }

    private Task getTask(int id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_EXISTED));
    }

    private Employee getEmployee(int employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "" : authentication.getName();
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .employeeId(task.getEmployee() == null ? 0 : task.getEmployee().getId())
                .employeeName(task.getEmployee() == null ? null : task.getEmployee().getName())
                .creationTime(task.getCreationTime())
                .dueDate(task.getDueDate())
                .build();
    }
}


