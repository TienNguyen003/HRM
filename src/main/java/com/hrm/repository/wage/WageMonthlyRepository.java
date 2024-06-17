package com.hrm.repository.wage;

import com.hrm.Entity.wage.WageMonthly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WageMonthlyRepository extends JpaRepository<WageMonthly, Integer> {
    Page<WageMonthly> findAll(Pageable pageable);

    @Query("SELECT w FROM WageMonthly w WHERE" +
            "(:wageCategories IS NULL OR w.wageCategories = :wageCategories) AND" +
            "(:time IS NULL OR w.time LIKE %:time%) AND" +
            "(:name IS NULL OR w.employee.name LIKE %:name%)"
            )
    Page<WageMonthly> findByTimeContainingAndWageCategories
            (String name, String time, String wageCategories, Pageable pageable);

    List<WageMonthly> findByEmployeeId(int employeeId);
}
