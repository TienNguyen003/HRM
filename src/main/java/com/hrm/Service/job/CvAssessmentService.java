package com.hrm.Service.job;

import com.hrm.Entity.job.Assessment;
import com.hrm.Entity.job.CvAssessmentScore;
import com.hrm.Entity.job.Requirements;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.dto.request.job.CvScoreRequest;
import com.hrm.repository.job.AssessmentRepository;
import com.hrm.repository.job.CvAssessmentScoreRepository;
import com.hrm.repository.job.DisqualifiedRepository;
import com.hrm.repository.job.RequireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Chấm điểm ứng viên theo tiêu chí lưu trong DB. */
@Service
@RequiredArgsConstructor
public class CvAssessmentService {
    private final CvAssessmentScoreRepository scoreRepository;
    private final DisqualifiedRepository disqualifiedRepository;
    private final AssessmentRepository assessmentRepository;
    private final RequireRepository requireRepository;

    @Transactional
    public Map<String, Object> scoreCandidate(int candidateId, CvScoreRequest request) {
        if (!disqualifiedRepository.existsById(candidateId))
            throw new AppException(ErrorCode.CV_NOT_EXISTED);

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new AppException(ErrorCode.CV_ASSESSMENT_NOT_FOUND));

        Map<Integer, Requirements> requirements = requireRepository.getByAssessmentId(assessment.getId()).stream()
                .collect(Collectors.toMap(Requirements::getId, Function.identity()));
        if (requirements.isEmpty())
            throw new AppException(ErrorCode.CV_SCORE_INVALID, "Bộ tiêu chí chưa có tiêu chí con");

        String actor = currentActor();
        List<CvAssessmentScore> saved = new ArrayList<>();
        double totalScore = 0;
        double maxScore = 0;

        for (CvScoreRequest.ScoreItem item : request.getScores()) {
            Requirements r = requirements.get(item.getRequirementId());
            if (r == null)
                throw new AppException(ErrorCode.CV_SCORE_INVALID,
                        "Tiêu chí " + item.getRequirementId() + " không thuộc bộ tiêu chí " + assessment.getId());
            double score = item.getScore() == null ? 0 : item.getScore();
            if (score < 0 || score > r.getWeight())
                throw new AppException(ErrorCode.CV_SCORE_INVALID,
                        "Điểm của '" + r.getTitle() + "' phải nằm trong [0, " + r.getWeight() + "]");

            CvAssessmentScore entity = scoreRepository.findByCandidateIdAndRequirementId(candidateId, r.getId())
                    .orElseGet(CvAssessmentScore::new);
            entity.setCandidateId(candidateId);
            entity.setRequirementId(r.getId());
            entity.setScore(score);
            entity.setComment(item.getComment());
            entity.setAssessedBy(actor);
            entity.setAssessedAt(LocalDateTime.now());
            saved.add(scoreRepository.save(entity));

            totalScore += score;
            maxScore += r.getWeight();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("candidateId", candidateId);
        result.put("assessmentId", assessment.getId());
        result.put("scores", saved.stream().map(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("requirementId", s.getRequirementId());
            m.put("title", requirements.get(s.getRequirementId()).getTitle());
            m.put("score", s.getScore());
            m.put("max", requirements.get(s.getRequirementId()).getWeight());
            m.put("comment", s.getComment());
            return m;
        }).toList());
        result.put("totalScore", totalScore);
        result.put("maxScore", maxScore);
        result.put("percentage", maxScore == 0 ? 0 : (int) Math.round(totalScore / maxScore * 100));
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getScores(int candidateId) {
        if (!disqualifiedRepository.existsById(candidateId))
            throw new AppException(ErrorCode.CV_NOT_EXISTED);
        List<CvAssessmentScore> scores = scoreRepository.findByCandidateIdOrderByRequirementIdAsc(candidateId);
        double total = scores.stream().mapToDouble(CvAssessmentScore::getScore).sum();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("candidateId", candidateId);
        result.put("scores", scores);
        result.put("totalScore", total);
        return result;
    }

    private String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName() != null ? auth.getName() : "system";
    }
}
