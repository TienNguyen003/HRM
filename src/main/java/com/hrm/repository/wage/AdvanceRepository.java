package com.hrm.repository.wage;

import com.hrm.Entity.wage.Advance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AdvanceRepository extends JpaRepository<Advance, Integer> {
    @Query("SELECT a FROM Advance a WHERE" +
            "(:status IS NULL OR a.status = :status) AND" +
            "(:id IS NULL OR a.employee.id = :id) AND " +
            "(:name IS NULL OR a.employee.name LIKE %:name%)")
    Page<Advance> findByNameAndStatus
            (String name, Integer status, Integer id, Pageable pageable);

    @Query("SELECT SUM(money) FROM Advance a WHERE" +
            "(:status IS NULL OR a.status = :status) AND" +
            "(:startRequestTime IS NULL OR a.requestTime >= :startRequestTime) AND" +
            "(:endRequestTime IS NULL OR a.requestTime <= :endRequestTime) AND" +
            "(:id IS NULL OR a.employee.id = :id)")
    int money(Integer id, Integer status, LocalDateTime startRequestTime, LocalDateTime endRequestTime);
}
