package com.hrm.Entity.hrm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Audit log cho các nghiệp vụ quan trọng của module HRM. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_audit_log", indexes = {
        @Index(name = "idx_hrm_audit_date", columnList = "date"),
        @Index(name = "idx_hrm_audit_entity", columnList = "entity, entity_id")
})
public class HrmAuditLog {
    @Id
    @Column(length = 60)
    private String id;

    @Column(nullable = false, length = 120)
    private String action;

    @Column(length = 80)
    private String entity;

    @Column(name = "entity_id", length = 80)
    private String entityId;

    @Column(length = 120)
    private String actor;

    @Column(nullable = false, length = 40)
    private String date;

    public HrmAuditLog(String id, String action, String entity, String entityId, String actor, String date) {
        this.id = id;
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.actor = actor;
        this.date = date;
    }
}
