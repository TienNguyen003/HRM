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
    @Query("SELECT w FROM WageMonthly w WHERE" +
            "(:type IS NULL OR w.wageCategories.salaryType = :type) AND" +
            "(:wageCategories IS NULL OR w.wageCategories.id = :wageCategories) AND"+
            "(:time IS NULL OR w.time LIKE %:time%) AND" +
            "(:employeeId IS NULL OR w.employee.id = :employeeId) AND" +
            "(:name IS NULL OR w.employee.name LIKE %:name%)" +
            "order by time, w.employee.id")
    Page<WageMonthly> findByTimeWage
            (String name, String time, Integer wageCategories, String type, Integer employeeId, Pageable pageable);

    @Query("SELECT w FROM WageMonthly w WHERE" +
            "(:time IS NULL OR w.time LIKE %:time%) AND" +
            "(:employeeId IS NULL OR w.employee.id = :employeeId)")
    List<WageMonthly> findByEmployeeId(int employeeId, String time);

    @Query("SELECT w FROM WageMonthly w WHERE" +
            "(:wageCategories IS NULL OR w.wageCategories.id = :wageCategories) AND" +
            "(:time IS NULL OR w.time = :time) AND" +
            "(:employeeId IS NULL OR w.employee.id = :employeeId)")
    WageMonthly existsByEmployeeCate(Integer employeeId, Integer wageCategories, String time);
}
