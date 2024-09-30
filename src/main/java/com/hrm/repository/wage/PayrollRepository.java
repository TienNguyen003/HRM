package com.hrm.repository.wage;

import com.hrm.Entity.wage.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Integer> {
    @Query("SELECT COUNT(p) > 0 FROM Payroll p WHERE p.employee.id = :employeeId AND p.time = :time")
    boolean existsByEmployeeAndTime(Integer employeeId, String time);

    @Query("SELECT p FROM Payroll p WHERE" +
            "(:time IS NULL OR p.time LIKE :time%) AND" +
            "(:status IS NULL OR p.status = :status) AND" +
            "(:id IS NULL OR p.employee.id = :id) AND" +
            "(:name IS NULL OR p.employee.name LIKE %:name%)")
    Page<Payroll> findByNameAndTime
            (String name, String time, Integer status, Pageable pageable, Integer id);
}
