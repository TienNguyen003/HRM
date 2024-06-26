package com.hrm.repository.wage;

import com.hrm.Entity.wage.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Integer> {
    Page<Payroll> findAll(Pageable pageable);

    @Query("SELECT p FROM Payroll p WHERE" +
            "(:time IS NULL OR p.time LIKE :time%) AND" +
            "(:name IS NULL OR p.employee.name LIKE %:name%)")
    Page<Payroll> findByNameAndTime
            (String name, String time, Pageable pageable);
}
