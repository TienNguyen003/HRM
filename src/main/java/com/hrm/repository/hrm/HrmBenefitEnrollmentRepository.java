package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmBenefitEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmBenefitEnrollmentRepository extends JpaRepository<HrmBenefitEnrollment, String> {
    List<HrmBenefitEnrollment> findAllByEmployeeId(String employeeId);
}
