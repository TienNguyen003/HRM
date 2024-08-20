package com.hrm.repository.wage;

import com.hrm.Entity.wage.Advance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvanceRepository extends JpaRepository<Advance, Integer> {
    @Query("SELECT a FROM Advance a WHERE" +
            "(:status IS NULL OR a.status = :status) AND" +
            "(:name IS NULL OR a.employee.name LIKE %:name%)")
    Page<Advance> findByNameAndStatus
            (String name, Integer status, Pageable pageable);

    @Query("SELECT SUM(money) FROM Advance a WHERE" +
            "(:status IS NULL OR a.status = :status) AND" +
            "(:id IS NULL OR a.employee.id = :id)")
    int money(Integer id, Integer status);
}
