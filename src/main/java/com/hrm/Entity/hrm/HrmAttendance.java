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

/** BA�º£n ghi chA�º¥m cĂ´ng. LiĂªn kA�º¿t nghiA�»‡p vA�»¥ tA�»›i nhĂ¢n viĂªn. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_attendance", indexes = {
        @Index(name = "idx_hrm_att_date", columnList = "att_date"),
        @Index(name = "idx_hrm_att_employee", columnList = "employee_id"),
        @Index(name = "idx_hrm_att_status", columnList = "status")
})
public class HrmAttendance {
    @Id
    @Column(length = 80)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "employee_name")
    private String employeeName;
    private String department;

    @Column(name = "att_date")
    private String date;
    @Column(name = "date_label")
    private String dateLabel;
    private String weekday;
    @Column(name = "check_in")
    private String checkIn;
    @Column(name = "check_out")
    private String checkOut;
    private String hours;
    @Column(name = "late_minutes")
    private int lateMinutes;
    private String status;

    public String getEmployeeId() {
        return employee == null ? null : employee.getHrmId();
    }
}
