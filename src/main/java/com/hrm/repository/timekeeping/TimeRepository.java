package com.hrm.repository.timekeeping;

import com.hrm.Entity.timekeeping.TimeKeeping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeRepository extends JpaRepository<TimeKeeping, Integer> {
    @Query("SELECT COUNT(*) FROM TimeKeeping t WHERE" +
            "(:id IS NULL OR t.employee.id = :id) AND" +
            "(:date IS NULL OR t.date = :date)")
    int exitsByDayId(int id, String date);

    @Query("SELECT t FROM TimeKeeping t WHERE" +
            "(:department IS NULL OR t.employee.department.name LIKE %:department%) AND" +
            "(:office IS NULL OR t.employee.department.officeI.name LIKE %:office%) AND" +
            "(:date IS NULL OR t.date LIKE %:date%) AND" +
            "(:id IS NULL OR t.employee.id = :id) AND" +
            "(:name IS NULL OR t.employee.name LIKE %:name%)")
    Page<TimeKeeping> findByName
            (String name, String date, String department, String office, Integer id, Pageable pageable);
}
