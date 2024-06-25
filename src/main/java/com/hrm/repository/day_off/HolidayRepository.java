package com.hrm.repository.day_off;

import com.hrm.Entity.day_off.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Integer> {
    boolean existsByName(String name);

    Page<Holiday> findAll(Pageable pageable);

    @Query("SELECT al FROM Holiday al WHERE " +
            "(:name IS NULL OR al.name LIKE %:name%)")
    Page<Holiday> findByName(String name, Pageable pageable);
}
