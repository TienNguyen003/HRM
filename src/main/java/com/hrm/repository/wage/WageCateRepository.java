package com.hrm.repository.wage;

import com.hrm.Entity.wage.WageCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WageCateRepository extends JpaRepository<WageCategories, Integer> {
    boolean existsByName(String name);

    Page<WageCategories> findAll(Pageable pageable);

    @Query("SELECT al FROM WageCategories al WHERE " +
            "(:name IS NULL OR al.name LIKE %:name%) AND " +
            "(:symbol IS NULL OR al.symbol LIKE %:symbol%) AND " +
            "(:salaryType IS NULL OR al.salaryType = :salaryType)")
    Page<WageCategories> findByNameContainingAndSymbolContainingAndSalaryType
            (String name, String symbol, String salaryType, Pageable pageable);
}
