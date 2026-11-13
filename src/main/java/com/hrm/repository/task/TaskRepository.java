package com.hrm.repository.task;

import com.hrm.Entity.task.Task;
import com.hrm.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Page<Task> findByTitleContaining(String title, Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByEmployeeIdAndStatus(int employeeId, TaskStatus status, Pageable pageable);

    Page<Task> findByEmployeeId(int employeeId, Pageable pageable);

    List<Task> findAllByEmployeeId(int employeeId);

    long countByStatus(TaskStatus status);
}
