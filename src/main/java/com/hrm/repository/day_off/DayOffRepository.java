package com.hrm.repository.day_off;

import com.hrm.Entity.day_off.DayOffCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DayOffRepository extends JpaRepository<DayOffCategories, Integer> {
    boolean existsByNameDay(String name);

    Page<DayOffCategories> findAll(Pageable pageable);

    @Query("SELECT al FROM DayOffCategories al WHERE " +
            "(:nameDay IS NULL OR al.nameDay LIKE %:nameDay%) AND " +
            "(:status IS NULL OR al.status = :status)")
    Page<DayOffCategories> findByNameDayContainingAndStatus(String nameDay, Integer status, Pageable pageable);
}
