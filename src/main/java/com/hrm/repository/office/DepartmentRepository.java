package com.hrm.repository.office;

import com.hrm.Entity.office.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepartmentRepository extends JpaRepository<Department, String> {
    boolean existsByName(String name);

    Page<Department> findAll(Pageable pageable);

    @Query("SELECT al FROM Department al WHERE " +
            "(:name IS NULL OR al.name LIKE %:name%) AND " +
            "(:shortName IS NULL OR al.shortName LIKE %:shortName%) AND " +
            "(:status IS NULL OR al.status = :status)")
    Page<Department> findByNameContainingAndStatus(String name, String shortName, Integer status, Pageable pageable);
}
