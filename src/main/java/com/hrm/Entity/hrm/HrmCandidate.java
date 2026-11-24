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
import java.util.List;
import java.util.Map;

/** Ứng viên tuyển dụng. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_candidate", indexes = {
        @Index(name = "idx_hrm_candidate_stage", columnList = "stage"),
        @Index(name = "idx_hrm_candidate_email", columnList = "email")
})
public class HrmCandidate implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    private String name;
    private String initials;
    private String role;
    private String stage;
    private String applied;
    private String location;
    private Double rating;
    private String email;
    private String phone;
    private String experience;
    private String source;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Map<String, Object>> stageHistory;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
