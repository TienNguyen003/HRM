package com.hrm.Entity.hrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashMap;
import java.util.Map;

/** Nhận xét/phản hồi hiệu suất. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_feedback")
public class HrmFeedback implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    private String tone;
    @Column(length = 1000)
    private String text;
    private String author;
    private String subject;
    private String created;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
