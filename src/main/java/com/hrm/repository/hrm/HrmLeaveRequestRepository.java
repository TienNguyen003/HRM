package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmLeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmLeaveRequestRepository extends JpaRepository<HrmLeaveRequest, String> {
    List<HrmLeaveRequest> findAllByEmployee_Id(Integer employeeId);

    void deleteAllByEmployee_Id(Integer employeeId);

    java.util.Optional<HrmLeaveRequest> findByLegacyId(int legacyId);

    long countByStatus(String status);
}
