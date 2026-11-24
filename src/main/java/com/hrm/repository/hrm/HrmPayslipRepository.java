package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmPayslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmPayslipRepository extends JpaRepository<HrmPayslip, String> {
    List<HrmPayslip> findAllByEmployee_Id(String employeeId);
}
