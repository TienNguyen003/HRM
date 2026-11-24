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

/** Phiếu lương được sinh ra từ một đợt chạy lương. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_payslip", indexes = {
        @Index(name = "idx_hrm_payslip_employee", columnList = "employee_id"),
        @Index(name = "idx_hrm_payslip_month", columnList = "pay_month")
})
public class HrmPayslip {
    @Id
    @Column(length = 40)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private HrmEmployee employee;

    @Column(name = "employee_name")
    private String employeeName;
    @Column(name = "pay_month")
    private String month;
    private Double basic;
    private Double allowances;
    private Double deductions;
    private Double net;
    private String status;
    private String created;

    public String getEmployeeId() {
        return employee == null ? null : employee.getId();
    }
}
