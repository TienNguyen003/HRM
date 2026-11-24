package com.hrm.Entity.hrm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Vị trí tuyển dụng. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_position", indexes = {
        @Index(name = "idx_hrm_position_status", columnList = "status"),
        @Index(name = "idx_hrm_position_dept", columnList = "department")
})
public class HrmPosition {
    @Id
    @Column(length = 40)
    private String id;
    private String title;
    private String department;
    private String location;
    private String type;
    private String status;
    private int candidates;
    private String created;
    @Column(name = "posted_date")
    private String postedDate;
    private int openings;
    @Column(length = 1000)
    private String description;
}
