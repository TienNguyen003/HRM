package com.hrm.repository.job;

import com.hrm.Entity.job.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {
    boolean existsByTitle(String title);

}
