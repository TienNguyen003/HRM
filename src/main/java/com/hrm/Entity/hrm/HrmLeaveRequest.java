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

/** Đơn xin nghỉ phép. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_leave_request", indexes = {
        @Index(name = "idx_hrm_leave_status", columnList = "status"),
        @Index(name = "idx_hrm_leave_employee", columnList = "employee_id")
})
public class HrmLeaveRequest {
    @Id
    @Column(length = 40)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private HrmEmployee employee;

    @Column(name = "employee_name")
    private String employeeName;
    private String type;
    private String start;
    private String end;
    private int days;
    private String status;
    private String approver;
    private String submitted;
    private String reason;

    public String getEmployeeId() {
        return employee == null ? null : employee.getId();
    }
}
