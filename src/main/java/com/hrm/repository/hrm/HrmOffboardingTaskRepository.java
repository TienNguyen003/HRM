package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmOffboardingTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmOffboardingTaskRepository extends JpaRepository<HrmOffboardingTask, String> {
    List<HrmOffboardingTask> findAllByEmployee_Id(String employeeId);
}
