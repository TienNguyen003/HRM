package com.hrm.repository.job;

import com.hrm.Entity.job.Disqualified;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DisqualifiedRepository extends JpaRepository<Disqualified, Integer> {
    boolean existsByEmailAndPhone(String email, String phone);

    @Query("SELECT d FROM Disqualified d WHERE " +
            "(:email IS NULL OR :email = '' OR d.email LIKE %:email%) AND " +
            "(:phone IS NULL OR :phone = '' OR d.phone LIKE %:phone%) AND " +
            "(:status IS NULL OR d.status = :status)")
    Page<Disqualified> findByFilters(@Param("email") String email,
                                     @Param("phone") String phone,
                                     @Param("status") Integer status,
                                     Pageable pageable);

    // số CV theo trạng thái (dùng cho dashboard)
    long countByStatus(int status);

    // pipeline: ứng viên theo tin tuyển dụng
    java.util.List<Disqualified> findByJobIdOrderByStageAsc(Integer jobId);

    // ứng viên chưa gắn tin tuyển dụng
    java.util.List<Disqualified> findByJobIdIsNull();
}
