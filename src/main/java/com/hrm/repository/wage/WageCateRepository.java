package com.hrm.repository.wage;

import com.hrm.Entity.wage.WageCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WageCateRepository extends JpaRepository<WageCategories, Integer> {
    boolean existsByName(String name);

    @Query("SELECT al FROM WageCategories al WHERE " +
            "(:salaryType IS NULL OR al.salaryType = :salaryType)")
    List<WageCategories> findBySalaryType(String salaryType);

    @Query("SELECT al FROM WageCategories al WHERE " +
            "(:name IS NULL OR al.name LIKE %:name%) AND " +
            "(:symbol IS NULL OR al.symbol LIKE %:symbol%) AND " +
            "(:salaryType IS NULL OR al.salaryType LIKE %:salaryType%)")
    Page<WageCategories> findByNameSymbolType
            (String name, String symbol, String salaryType, Pageable pageable);
}
