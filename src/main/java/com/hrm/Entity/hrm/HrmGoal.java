package com.hrm.Entity.hrm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Mục tiêu hiệu suất của nhân viên. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_goal", indexes = {
        @Index(name = "idx_hrm_goal_status", columnList = "status"),
        @Index(name = "idx_hrm_goal_owner", columnList = "owner")
})
public class HrmGoal {
    @Id
    @Column(length = 40)
    private String id;
    private String title;
    private String owner;
    private String department;
    private int progress;
    private String due;
    @Column(name = "due_iso")
    private String dueIso;
    private String status;
    private String priority;
    private String metric;
    private String target;
}
