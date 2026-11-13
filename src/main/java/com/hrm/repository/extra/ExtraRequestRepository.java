package com.hrm.repository.extra;

import com.hrm.Entity.extra.ExtraRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraRequestRepository extends JpaRepository<ExtraRequest, Integer> {
    @Query("SELECT e FROM ExtraRequest e WHERE " +
            "(:name IS NULL OR e.employee.name LIKE %:name%) AND " +
            "(:type IS NULL OR e.type = :type) AND " +
            "(:status IS NULL OR e.status = :status) AND " +
            "(:employeeId IS NULL OR e.employee.id = :employeeId)")
    Page<ExtraRequest> findByFilters(@Param("name") String name,
                                     @Param("type") Integer type,
                                     @Param("status") Integer status,
                                     @Param("employeeId") Integer employeeId,
                                     Pageable pageable);

    // tổng giờ tăng ca đã duyệt trong tháng (date yyyy-MM-dd, prefix yyyy-MM)
    @Query("SELECT COALESCE(SUM(e.hours), 0) FROM ExtraRequest e " +
            "WHERE e.type = 1 AND e.status = 1 AND e.employee.id = :employeeId AND e.date LIKE :yearMonth%")
    double sumApprovedOvertime(@Param("employeeId") int employeeId, @Param("yearMonth") String yearMonth);

    // số đơn chờ duyệt (dashboard / nhắc việc)
    long countByStatus(int status);
}