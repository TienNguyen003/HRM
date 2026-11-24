package com.hrm.Entity.hrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Lịch phỏng vấn ứng viên. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_interview", indexes = {
        @Index(name = "idx_hrm_interview_candidate", columnList = "candidate_id"),
        @Index(name = "idx_hrm_interview_date", columnList = "iv_date")
})
public class HrmInterview {
    @Id
    @Column(length = 40)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "candidate_id", nullable = false)
    private HrmCandidate candidate;

    @Column(name = "candidate_name")
    private String candidateName;
    private String role;
    @Column(name = "iv_date")
    private String date;
    private String time;
    private int duration;
    private String mode;
    private String interviewer;
    private String status;

    public String getCandidateId() {
        return candidate == null ? null : candidate.getId();
    }
}
