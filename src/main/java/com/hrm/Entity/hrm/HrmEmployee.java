package com.hrm.Entity.hrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/** Nhân viên trong module HRM (id dạng EMP-00123). */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_employee", indexes = {
        @Index(name = "idx_hrm_emp_department", columnList = "department"),
        @Index(name = "idx_hrm_emp_status", columnList = "status"),
        @Index(name = "idx_hrm_emp_email", columnList = "email")
})
public class HrmEmployee {
    @Id
    @Column(length = 20)
    private String id;

    private String firstName;
    private String lastName;
    private String name;
    private String initials;
    private String position;
    private String department;
    private String manager;
    @Column(name = "employment_type")
    private String employmentType;
    private String status;

    @Column(name = "joined_date")
    private String joinedDate;
    @Column(name = "joined_date_iso")
    private String joinedDateIso;

    private String email;
    private String phone;
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    private String address;

    @JsonIgnore
    @Column(name = "emergency_name")
    private String emergencyName;
    @JsonIgnore
    @Column(name = "emergency_relation")
    private String emergencyRelation;
    @JsonIgnore
    @Column(name = "emergency_phone")
    private String emergencyPhone;

    private String location;
    @Column(name = "id_card")
    private String idCard;
    @Column(name = "bank_account")
    private String bankAccount;
    @Column(name = "probation_end_date")
    private String probationEndDate;
    private String avatar;

    @Column(name = "attendance_rate")
    private int attendanceRate;
    @Column(name = "leave_balance")
    private int leaveBalance;
    private int goals;

    public String getEmployeeId() {
        return id;
    }

    @JsonProperty("emergencyContact")
    public Map<String, Object> emergencyContact() {
        Map<String, Object> contact = new LinkedHashMap<>();
        contact.put("name", emergencyName == null ? "" : emergencyName);
        contact.put("relation", emergencyRelation == null ? "" : emergencyRelation);
        contact.put("phone", emergencyPhone == null ? "" : emergencyPhone);
        return contact;
    }
}
