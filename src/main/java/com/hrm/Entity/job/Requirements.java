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

/** Tiêu chí đánh giá CV — được dùng trực tiếp bởi /api/cv/filter. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "requirements", indexes = @Index(name = "idx_require_assessment", columnList = "assessment_id"))
public class Requirements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String title;

    @Column(name = "assessment_id", nullable = false)
    int assessmentId;

    /** Từ khóa cần tìm trong CV. Nếu trống thì dùng title. */
    @Column(length = 500)
    String keyword;

    /** Trọng số / điểm tối đa của tiêu chí. */
    @Builder.Default
    int weight = 1;

    /** true = tiêu chí bắt buộc; thiếu thì loại CV. */
    @Builder.Default
    boolean mandatory = false;
}
