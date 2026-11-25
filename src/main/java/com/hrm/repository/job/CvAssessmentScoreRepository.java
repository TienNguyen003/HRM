package com.hrm.repository.job;

import com.hrm.Entity.job.CvAssessmentScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CvAssessmentScoreRepository extends JpaRepository<CvAssessmentScore, Integer> {
    List<CvAssessmentScore> findByCandidateIdOrderByRequirementIdAsc(int candidateId);

    Optional<CvAssessmentScore> findByCandidateIdAndRequirementId(int candidateId, int requirementId);

    void deleteByCandidateId(int candidateId);

    boolean existsByCandidateId(int candidateId);
}
