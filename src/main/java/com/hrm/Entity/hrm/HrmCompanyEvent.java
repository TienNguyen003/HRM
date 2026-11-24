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

/** Sự kiện công ty. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_company_event", indexes = {
        @Index(name = "idx_hrm_event_date", columnList = "event_date")
})
public class HrmCompanyEvent implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    private String title;
    @Column(name = "event_date")
    private String date;
    private String location;
    @Column(length = 1000)
    private String description;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
