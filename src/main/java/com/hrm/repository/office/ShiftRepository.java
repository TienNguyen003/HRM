package com.hrm.repository.office;

import com.hrm.Entity.office.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    Page<Shift> findAll(Pageable pageable);
}
