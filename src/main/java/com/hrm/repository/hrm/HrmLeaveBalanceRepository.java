package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmLeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmLeaveBalanceRepository extends JpaRepository<HrmLeaveBalance, String> {
    List<HrmLeaveBalance> findByEmployee_Id(String employeeId);

    void deleteByEmployee_Id(String employeeId);
}
