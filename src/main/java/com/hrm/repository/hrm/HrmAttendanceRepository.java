package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmAttendanceRepository extends JpaRepository<HrmAttendance, String> {
    List<HrmAttendance> findAllByEmployee_Id(Integer employeeId);

    void deleteAllByEmployee_Id(Integer employeeId);
}
