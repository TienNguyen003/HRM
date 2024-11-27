package com.hrm.repository.job;

import com.hrm.Entity.job.Requirements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequireRepository extends JpaRepository<Requirements, Integer> {
    boolean existsByTitle(String title);

    @Query("SELECT r FROM Requirements r WHERE r.assessmentId = :assessId")
    List<Requirements> getByAssessmentId(int assessId);
}
