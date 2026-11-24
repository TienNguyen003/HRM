package com.hrm.repository.hrm;

import com.hrm.Entity.hrm.HrmInterview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HrmInterviewRepository extends JpaRepository<HrmInterview, String> {
    List<HrmInterview> findAllByCandidate_Id(String candidateId);
}
