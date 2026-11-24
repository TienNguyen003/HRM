package com.hrm.Entity.hrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashMap;
import java.util.Map;

/** Đánh giá hiệu suất định kỳ. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_review", indexes = {
        @Index(name = "idx_hrm_review_status", columnList = "status"),
        @Index(name = "idx_hrm_review_employee", columnList = "employee_id")
})
public class HrmReview implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    private String employee;
    @Column(name = "employee_id")
    private String employeeId;
    private String cycle;
    private String reviewer;
    private Double score;
    private String status;
    private String date;
    @Column(length = 1000)
    private String strengths;
    @Column(length = 1000)
    private String improvements;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
