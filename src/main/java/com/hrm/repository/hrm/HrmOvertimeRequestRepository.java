package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmOvertimeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmOvertimeRequestRepository extends JpaRepository<HrmOvertimeRequest, String> {
    List<HrmOvertimeRequest> findAllByEmployee_Id(Integer employeeId);
}
