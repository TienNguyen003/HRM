package com.hrm.Entity.job;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/** Bộ tiêu chí đánh giá, có thể gắn với một tin tuyển dụng. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "assessment", indexes = @Index(name = "idx_assessment_job", columnList = "job_id"))
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String title;
    int requirement;

    /** Tin tuyển dụng áp dụng bộ tiêu chí này (nullable). */
    @Column(name = "job_id")
    Integer jobId;
}
