package com.hrm.hrm;

import com.hrm.Entity.job.Assessment;
import com.hrm.Entity.job.Disqualified;
import com.hrm.Entity.job.Requirements;
import com.hrm.Exception.AppException;
import com.hrm.Service.job.CvAssessmentService;
import com.hrm.dto.request.job.CvScoreRequest;
import com.hrm.repository.job.AssessmentRepository;
import com.hrm.repository.job.CvAssessmentScoreRepository;
import com.hrm.repository.job.DisqualifiedRepository;
import com.hrm.repository.job.RequireRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Kiểm chứng tiêu chí DB được dùng để chấm điểm và lưu lại. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CvAssessmentServiceTest {

    @Autowired CvAssessmentService service;
    @Autowired AssessmentRepository assessmentRepository;
    @Autowired RequireRepository requireRepository;
    @Autowired DisqualifiedRepository candidateRepository;
    @Autowired CvAssessmentScoreRepository scoreRepository;

    private int setupAssessment() {
        Assessment a = assessmentRepository.save(Assessment.builder()
                .title("Backend CV criteria").requirement(2).jobId(7).build());
        Requirements r1 = requireRepository.save(Requirements.builder()
                .title("Java experience").keyword("Java").assessmentId(a.getId()).weight(3).mandatory(true).build());
        Requirements r2 = requireRepository.save(Requirements.builder()
                .title("Spring Boot").keyword("Spring").assessmentId(a.getId()).weight(2).mandatory(false).build());
        assertThat(r1.getId()).isPositive();
        assertThat(r2.getId()).isPositive();
        return a.getId();
    }

    private CvScoreRequest.ScoreItem item(int requirementId, double score) {
        CvScoreRequest.ScoreItem i = new CvScoreRequest.ScoreItem();
        i.setRequirementId(requirementId);
        i.setScore(score);
        i.setComment("checked");
        return i;
    }

    @Test
    void scoreCandidate_persistsScoresAndComputesTotal() {
        int assessmentId = setupAssessment();
        int candidateId = candidateRepository.save(Disqualified.builder()
                .email("cv@test.io").status(2).build()).getId();
        int r1 = requireRepository.getByAssessmentId(assessmentId).get(0).getId();
        int r2 = requireRepository.getByAssessmentId(assessmentId).get(1).getId();

        CvScoreRequest request = new CvScoreRequest();
        request.setAssessmentId(assessmentId);
        request.setScores(List.of(item(r1, 2.5), item(r2, 2)));

        Map<String, Object> result = service.scoreCandidate(candidateId, request);

        assertThat(result.get("totalScore")).isEqualTo(4.5);
        assertThat(result.get("maxScore")).isEqualTo(5.0);
        assertThat(result.get("percentage")).isEqualTo(90);
        assertThat(scoreRepository.findByCandidateIdOrderByRequirementIdAsc(candidateId)).hasSize(2);
        assertThat(service.getScores(candidateId).get("scores")).isNotNull();
    }

    @Test
    void scoreCandidate_rejectsInvalidScoreAndForeignRequirement() {
        int assessmentId = setupAssessment();
        int candidateId = candidateRepository.save(Disqualified.builder()
                .email("cv2@test.io").status(2).build()).getId();
        int r1 = requireRepository.getByAssessmentId(assessmentId).get(0).getId();

        CvScoreRequest tooHigh = new CvScoreRequest();
        tooHigh.setAssessmentId(assessmentId);
        tooHigh.setScores(List.of(item(r1, 99)));
        assertThatThrownBy(() -> service.scoreCandidate(candidateId, tooHigh))
                .isInstanceOf(AppException.class);

        CvScoreRequest foreign = new CvScoreRequest();
        foreign.setAssessmentId(assessmentId);
        foreign.setScores(List.of(item(99999, 1)));
        assertThatThrownBy(() -> service.scoreCandidate(candidateId, foreign))
                .isInstanceOf(AppException.class);
    }
}
