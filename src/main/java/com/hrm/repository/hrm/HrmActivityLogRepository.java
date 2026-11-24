package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmActivityLogRepository extends JpaRepository<HrmActivityLog, String> {
    List<HrmActivityLog> findAllByEmployeeId(String employeeId);
}
