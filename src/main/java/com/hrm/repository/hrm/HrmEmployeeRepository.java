package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HrmEmployeeRepository extends JpaRepository<HrmEmployee, String> {
}
