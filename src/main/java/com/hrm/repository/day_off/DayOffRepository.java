package com.hrm.repository.day_off;

import com.hrm.Entity.day_off.DayOffCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DayOffRepository extends JpaRepository<DayOffCategories, Integer> {
    boolean existsByNameDay(String name);

    @Query("SELECT d FROM DayOffCategories d WHERE d.status = 1")
    List<DayOffCategories> getAllDayOff();

    @Query("SELECT d FROM DayOffCategories d WHERE " +
            "(:nameDay IS NULL OR d.nameDay LIKE %:nameDay%) AND " +
            "(:status IS NULL OR d.status = :status)")
    Page<DayOffCategories> findByNameAndStatus(String nameDay, Integer status, Pageable pageable);
}
