package com.hrm.Entity.hrm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Theo dõi các lần seed để đảm bảo idempotent và không ghi đè dữ liệu người dùng. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_seed_log")
public class HrmSeedLog {
    @Id
    @Column(length = 100)
    private String seedKey;

    private String description;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "row_count")
    private int rowCount;

    private String status;

    public HrmSeedLog(String seedKey, String description, int rowCount, String status) {
        this.seedKey = seedKey;
        this.description = description;
        this.rowCount = rowCount;
        this.status = status;
        this.executedAt = LocalDateTime.now();
    }
}
