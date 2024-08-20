package com.hrm.repository.office;

import com.hrm.Entity.office.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("SELECT COUNT(*) > 0 FROM Department d WHERE d.name = :name AND d.officeI.id = :officeId")
    boolean existsByNameAndId(String name, Integer officeId);

    @Query("SELECT d FROM Department d WHERE d.status = 1")
    List<Department> getAllDepartment();

    @Query("SELECT d FROM Department d WHERE " +
            "(:name IS NULL OR d.name LIKE %:name%) AND " +
            "(:shortName IS NULL OR d.shortName LIKE %:shortName%) AND " +
            "(:status IS NULL OR d.status = :status)")
    Page<Department> findByNameAndStatus(String name, String shortName, Integer status, Pageable pageable);
}
