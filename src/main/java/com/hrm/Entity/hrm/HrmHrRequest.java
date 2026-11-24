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

/** Yêu cầu nhân sự (HR request) do nhân viên gửi hoặc HR tạo. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_hr_request", indexes = {
        @Index(name = "idx_hrm_hrreq_status", columnList = "status"),
        @Index(name = "idx_hrm_hrreq_employee", columnList = "employee_id")
})
public class HrmHrRequest implements HrmAttributesAware {
    @Id
    @Column(length = 40)
    private String id;
    @Column(name = "employee_id")
    private String employeeId;
    @Column(name = "employee_name")
    private String employeeName;
    private String type;
    private String subject;
    @Column(length = 1000)
    private String description;
    private String status;
    private String created;

    @JsonIgnore
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
