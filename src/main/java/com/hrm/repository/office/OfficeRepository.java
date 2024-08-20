package com.hrm.repository.office;

import com.hrm.Entity.office.OfficeI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OfficeRepository extends JpaRepository<OfficeI, Integer> {
    boolean existsByName(String name);

    @Query("SELECT o FROM OfficeI o WHERE o.status = 1")
    List<OfficeI> getOffices();

    @Query("SELECT o FROM OfficeI o WHERE " +
            "(:name IS NULL OR o.name LIKE %:name%) AND " +
            "(:status IS NULL OR o.status = :status)")
    Page<OfficeI> findByNameAndStatus(String name, Integer status, Pageable pageable);
}
