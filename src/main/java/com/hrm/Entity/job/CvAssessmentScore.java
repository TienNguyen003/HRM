package com.hrm.Entity.job;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/** Điểm đánh giá của một ứng viên theo từng tiêu chí. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "cv_assessment_score", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cv_score_candidate_require", columnNames = {"candidate_id", "requirement_id"})
}, indexes = @Index(name = "idx_cv_score_candidate", columnList = "candidate_id"))
public class CvAssessmentScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    /** Disqualified.id — ứng viên trong pipeline lọc CV. */
    @Column(name = "candidate_id", nullable = false)
    int candidateId;

    @Column(name = "requirement_id", nullable = false)
    int requirementId;

    @Column(nullable = false)
    double score;

    @Column(length = 500)
    String comment;

    @Column(name = "assessed_by")
    String assessedBy;

    @Column(name = "assessed_at", nullable = false)
    LocalDateTime assessedAt;
}
