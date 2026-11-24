package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmHrRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmHrRequestRepository extends JpaRepository<HrmHrRequest, String> {
    List<HrmHrRequest> findAllByEmployeeId(String employeeId);
}
