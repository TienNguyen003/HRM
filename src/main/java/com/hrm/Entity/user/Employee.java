package com.hrm.Entity.user;

import com.hrm.Entity.office.Department;
import com.hrm.Entity.office.OfficeI;
import com.hrm.Entity.wage.Formula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Nhân viên — entity DUY NHẤT của hệ thống.
 *
 * Module cũ (/api/employee, lương, hợp đồng, chấm công...) dùng id số (int).
 * Module HRM (/api/hrm/*) dùng employeeCode dạng EMP-00123; code này được sinh từ
 * id số khi chưa có, nên không cần bảng nhân viên thứ hai.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "employee", uniqueConstraints = {
        @UniqueConstraint(name = "uk_employee_code", columnNames = "employee_code")
}, indexes = {
        @Index(name = "idx_employee_department_name", columnList = "department_name"),
        @Index(name = "idx_employee_status", columnList = "status")
})
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    /** Mã nhân viên EMP-00123 dùng bởi module /api/hrm. Có thể null với bản ghi cũ. */
    @Column(name = "employee_code", length = 20)
    String employeeCode;

    // ---------- field của module cũ (giữ nguyên để không phá API cũ) ----------
    String name;
    String email;
    @Column(name = "phone_number")
    String phone_number;
    String gender;
    String image;
    @Column(name = "birth_date")
    String birth_date;
    @Column(name = "joined_date")
    String joined_date;
    /** @deprecated không còn được dùng; giữ cột/DTO chỉ để API cũ không vỡ. */
    @Deprecated
    @Column(name = "shift_id")
    int shift_id;
    /** @deprecated số dư nghỉ giờ nằm ở hrm_leave_balance. */
    @Deprecated
    @Column(name = "vacation_time")
    float vacationTime;
    /** @deprecated không còn được cập nhật sau khi gom nghỉ phép. */
    @Deprecated
    @Column(name = "hour_off")
    int hourOff;
    /** @deprecated số dư nghỉ giờ nằm ở hrm_leave_balance. */
    @Deprecated
    @Column(name = "vacation_hours")
    int vacationHours;
    int timekeeping;
    int lateness;

    @ManyToOne
    Department department;

    /** Địa điểm làm việc — nguồn là OfficeI; location ở trên chỉ là cache để trả response. */
    @ManyToOne
    @JoinColumn(name = "office_id")
    OfficeI office;

    @ManyToOne
    Formula formula;

    // ---------- field bổ sung cho module HRM ----------
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    String initials;
    String position;
    @Column(name = "department_name")
    String departmentName;
    String manager;
    @Column(name = "employment_type")
    String employmentType;
    String status;
    @Column(name = "joined_date_iso")
    String joinedDateIso;
    String location;
    String address;
    @Column(name = "emergency_name")
    String emergencyName;
    @Column(name = "emergency_relation")
    String emergencyRelation;
    @Column(name = "emergency_phone")
    String emergencyPhone;
    @Column(name = "id_card")
    String idCard;
    @Column(name = "bank_account")
    String bankAccount;
    @Column(name = "probation_end_date")
    String probationEndDate;
    String avatar;
    @Column(name = "attendance_rate")
    int attendanceRate;
    @Column(name = "leave_balance")
    int leaveBalance;
    int goals;

    /** Mã dùng bởi module /api/hrm (EMP-00123). Dòng cũ chưa có code thì suy từ id. */
    @jakarta.persistence.Transient
    public String getHrmId() {
        if (employeeCode != null && !employeeCode.isBlank()) return employeeCode;
        return String.format("EMP-%05d", id);
    }
}
