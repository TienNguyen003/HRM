package com.hrm.repository.day_off;

import com.hrm.Entity.day_off.SabbaticalLeaveLogs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SabLogsRepository extends JpaRepository<SabbaticalLeaveLogs, Integer> {

    @Query("SELECT al FROM SabbaticalLeaveLogs al WHERE " +
            "(:id IS NULL OR al.employee.id = :id) AND " +
            "(:name IS NULL OR al.employee.name LIKE %:name%)")
    Page<SabbaticalLeaveLogs> findByNameContaining(String name, Integer id, Pageable pageable);
}
