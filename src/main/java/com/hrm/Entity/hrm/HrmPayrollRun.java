package com.hrm.Entity.hrm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Đợt chạy bảng lương. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_payroll_run", indexes = {
        @Index(name = "idx_hrm_payroll_month", columnList = "pay_month"),
        @Index(name = "idx_hrm_payroll_status", columnList = "status")
})
public class HrmPayrollRun {
    @Id
    @Column(length = 40)
    private String id;
    @Column(name = "pay_month")
    private String month;
    private String department;
    private Double amount;
    private String status;
    private String created;
}
