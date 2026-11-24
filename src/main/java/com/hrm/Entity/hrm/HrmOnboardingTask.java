package com.hrm.Entity.hrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Nhiệm vụ onboarding nhân viên mới. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_onboarding_task", indexes = {
        @Index(name = "idx_hrm_onb_employee", columnList = "employee_id"),
        @Index(name = "idx_hrm_onb_status", columnList = "status")
})
public class HrmOnboardingTask {
    @Id
    @Column(length = 40)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private HrmEmployee employee;

    @Column(name = "employee_name")
    private String employeeName;
    private String task;
    private String owner;
    @Column(name = "due_date")
    private String dueDate;
    private String status;
    private String created;

    public String getEmployeeId() {
        return employee == null ? null : employee.getId();
    }
}
