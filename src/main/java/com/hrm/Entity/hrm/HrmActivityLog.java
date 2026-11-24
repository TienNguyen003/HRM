package com.hrm.Entity.hrm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Lịch sử hoạt động trên hồ sơ nhân viên. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_activity_log", indexes = {
        @Index(name = "idx_hrm_activity_employee", columnList = "employee_id")
})
public class HrmActivityLog {
    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "employee_id", length = 40)
    private String employeeId;
    private String type;
    private String detail;
    private String date;
    private String icon;
    private String tone;

    public HrmActivityLog(String id, String employeeId, String type, String detail, String date, String icon, String tone) {
        this.id = id;
        this.employeeId = employeeId;
        this.type = type;
        this.detail = detail;
        this.date = date;
        this.icon = icon;
        this.tone = tone;
    }
}
