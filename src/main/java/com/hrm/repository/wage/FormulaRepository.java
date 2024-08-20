package com.hrm.repository.wage;

import com.hrm.Entity.wage.Formula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FormulaRepository extends JpaRepository<Formula, Integer> {
    @Query("SELECT f FROM Formula f WHERE" +
            "(:name IS NULL OR f.name LIKE %:name%) AND" +
            "(:status IS NULL OR f.status = :status)")
    Page<Formula> findByNameStatus(String name, Integer status, Pageable pageable);
}
