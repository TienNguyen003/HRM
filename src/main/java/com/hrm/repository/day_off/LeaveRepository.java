package com.hrm.repository.day_off;

import com.hrm.Entity.day_off.ApplicationLeave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends JpaRepository<ApplicationLeave, Integer> {

    @Query("SELECT al FROM ApplicationLeave al WHERE " +
            "(:name IS NULL OR al.employee.name LIKE %:name%) AND " +
            "(:dayOff IS NULL OR al.dayOffCategories.id = :dayOff) AND " +
            "(:status IS NULL OR al.status = :status)")
    Page<ApplicationLeave> findByNameContainingAndDayOffAndStatus(
            String name, Integer dayOff, Integer status, Pageable pageable);
}
