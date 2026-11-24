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

/** Gói phúc lợi. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_benefit")
public class HrmBenefit implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    private String name;
    private String type;
    @Column(length = 1000)
    private String description;
    private String status;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
