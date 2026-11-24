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

/** Chỉ số hiệu suất (KPI). */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_kpi")
public class HrmKpi implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    private String name;
    private String owner;
    private String department;
    private Double current;
    private Double target;
    private String unit;
    private Double trend;
    private String status;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
