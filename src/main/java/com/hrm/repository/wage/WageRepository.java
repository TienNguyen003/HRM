package com.hrm.repository.wage;

import com.hrm.Entity.wage.Wage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WageRepository extends JpaRepository<Wage, Integer> {
    @Query("SELECT w FROM Wage w WHERE" +
            "(:name IS NULL OR w.employee.name LIKE %:name%) AND" +
            "(:type IS NULL OR w.wageCategories.salaryType = :type) AND" +
            "(:employeeId IS NULL OR w.employee.id = :employeeId) AND" +
            "(:wageCategories IS NULL OR w.wageCategories.id = :wageCategories)" +
            "ORDER BY w.employee.name")
    Page<Wage> findByNameAndWage
            (String name, Integer wageCategories, String type, Integer employeeId, Pageable pageable);

    @Query("SELECT w FROM Wage w WHERE" +
            "(:employeeId IS NULL OR w.employee.id = :employeeId)")
    List<Wage> findByEmployee(Integer employeeId);

    @Query("SELECT w FROM Wage w WHERE" +
            "(:wageCategories IS NULL OR w.wageCategories.id = :wageCategories) AND" +
            "(:employeeId IS NULL OR w.employee.id = :employeeId)")
    Wage existsByEmployeeCate(Integer employeeId, Integer wageCategories);
}
