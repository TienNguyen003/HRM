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

/** Phân ca làm việc theo tháng/phòng ban. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_roster", indexes = {
        @Index(name = "idx_hrm_roster_month", columnList = "roster_month"),
        @Index(name = "idx_hrm_roster_employee", columnList = "employee_id")
})
public class HrmRoster implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    @Column(name = "employee_id")
    private String employeeId;
    @Column(name = "employee_name")
    private String employeeName;
    @Column(name = "roster_month")
    private String month;
    private String date;
    private String shift;
    private String department;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
