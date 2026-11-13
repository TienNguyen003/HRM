package com.hrm.repository.job;

import com.hrm.Entity.job.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Integer> {
    boolean existsByTitle(String title);

    @Query("SELECT j FROM JobPosting j WHERE " +
            "(:title IS NULL OR j.title LIKE %:title%) AND " +
            "(:status IS NULL OR j.status = :status)")
    Page<JobPosting> findByFilters(@Param("title") String title,
                                   @Param("status") Integer status,
                                   Pageable pageable);

    long countByStatus(int status);
}