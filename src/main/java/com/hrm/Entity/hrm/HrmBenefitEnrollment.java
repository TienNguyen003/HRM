package com.hrm.Entity.hrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashMap;
import java.util.Map;

/** Đăng ký tham gia phúc lợi của nhân viên. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_benefit_enrollment", indexes = {
        @Index(name = "idx_hrm_be_employee", columnList = "employee_id"),
        @Index(name = "idx_hrm_be_benefit", columnList = "benefit_id")
})
public class HrmBenefitEnrollment implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    @Column(name = "employee_id")
    private String employeeId;
    @Column(name = "employee_name")
    private String employeeName;
    @Column(name = "benefit_id")
    private String benefitId;
    @Column(name = "benefit_name")
    private String benefitName;
    @Column(name = "enrolled_date")
    private String enrolledDate;
    private String status;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
