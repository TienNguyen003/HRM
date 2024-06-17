package com.hrm.repository.wage;

import com.hrm.Entity.wage.Wage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WageRepository extends JpaRepository<Wage, Integer> {
    Page<Wage> findAll(Pageable pageable);

    @Query("SELECT w FROM Wage w WHERE" +
            "(:name IS NULL OR w.employee.name LIKE %:name%) AND" +
            "(:wageCategories IS NULL OR w.wageCategories = :wageCategories)")
    Page<Wage> findByNameContainingAndWageCategories
            (String name, String wageCategories, Pageable pageable);
}
