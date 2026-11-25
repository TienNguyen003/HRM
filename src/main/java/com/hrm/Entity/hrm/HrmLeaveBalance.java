package com.hrm.Entity.hrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hrm.Entity.user.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/** SĂ¡Â»â€˜ dĂ†Â° nghĂ¡Â»â€° phÄ‚Â©p theo tĂ¡Â»Â«ng loĂ¡ÂºÂ¡i (annual/sick/unpaid). */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_leave_balance")
public class HrmLeaveBalance {
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

    @JsonIgnore private int annualTotal;
    @JsonIgnore private int annualUsed;
    @JsonIgnore private int annualPending;
    @JsonIgnore private int sickTotal;
    @JsonIgnore private int sickUsed;
    @JsonIgnore private int sickPending;
    @JsonIgnore private int unpaidTotal;
    @JsonIgnore private int unpaidUsed;
    @JsonIgnore private int unpaidPending;

    public String getEmployeeId() {
        return employee == null ? null : employee.getHrmId();
    }

    public Map<String, Object> getAnnual() {
        return bucket(annualTotal, annualUsed, annualPending);
    }

    public Map<String, Object> getSick() {
        return bucket(sickTotal, sickUsed, sickPending);
    }

    public Map<String, Object> getUnpaid() {
        return bucket(unpaidTotal, unpaidUsed, unpaidPending);
    }

    private Map<String, Object> bucket(int total, int used, int pending) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("total", total);
        m.put("used", used);
        m.put("pending", pending);
        return m;
    }
}
