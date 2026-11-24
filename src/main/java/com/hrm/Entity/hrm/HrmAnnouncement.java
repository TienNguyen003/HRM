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

/** Thông báo công ty. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_announcement")
public class HrmAnnouncement implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    private String title;
    @Column(length = 2000)
    private String content;
    private String author;
    private String created;
    private boolean pinned;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
