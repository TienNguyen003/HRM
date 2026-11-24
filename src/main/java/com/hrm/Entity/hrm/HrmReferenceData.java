package com.hrm.Entity.hrm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dữ liệu tham chiếu/cấu hình (Reference & Configuration Seed). */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_reference_data", indexes = {
        @Index(name = "idx_hrm_ref_category", columnList = "category")
})
public class HrmReferenceData {
    @Id
    @Column(length = 200)
    private String id;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, length = 120)
    private String code;

    private String label;

    @Column(name = "sort_order")
    private int sortOrder;

    public HrmReferenceData(String id, String category, String code, String label, int sortOrder) {
        this.id = id;
        this.category = category;
        this.code = code;
        this.label = label;
        this.sortOrder = sortOrder;
    }
}
