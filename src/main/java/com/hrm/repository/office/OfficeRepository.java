package com.hrm.repository.office;

import com.hrm.Entity.office.OfficeI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OfficeRepository extends JpaRepository<OfficeI, Integer> {
    boolean existsByName(String name);

    Page<OfficeI> findAll(Pageable pageable);

    @Query("SELECT al FROM OfficeI al WHERE " +
            "(:name IS NULL OR al.name LIKE %:name%) AND " +
            "(:status IS NULL OR al.status = :status)")
    Page<OfficeI> findByNameContainingAndStatus(String name, Integer status, Pageable pageable);
}
