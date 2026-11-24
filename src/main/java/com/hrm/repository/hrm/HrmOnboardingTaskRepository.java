package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmOnboardingTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmOnboardingTaskRepository extends JpaRepository<HrmOnboardingTask, String> {
    List<HrmOnboardingTask> findAllByEmployee_Id(String employeeId);
}
