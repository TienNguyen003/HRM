package com.hrm.Entity.hrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hrm.Entity.user.Employee;
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

/** Ă„ÂĂ†Â¡n Ă„â€˜Ă„Æ’ng kÄ‚Â½ tĂ„Æ’ng ca. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_overtime_request", indexes = {
        @Index(name = "idx_hrm_ot_status", columnList = "status"),
        @Index(name = "idx_hrm_ot_employee", columnList = "employee_id")
})
public class HrmOvertimeRequest {
    @Id
    @Column(length = 40)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "employee_name")
    private String employeeName;
    private String department;
    private String date;
    private Double hours;
    @Column(length = 1000)
    private String reason;
    private String status;
    private String approver;
    private String submitted;

    public String getEmployeeId() {
        return employee == null ? null : employee.getHrmId();
    }
}
