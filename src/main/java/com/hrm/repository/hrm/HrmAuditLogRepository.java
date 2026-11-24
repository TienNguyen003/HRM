package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmAuditLogRepository extends JpaRepository<HrmAuditLog, String> {
    List<HrmAuditLog> findAllByOrderByDateDesc();
}
