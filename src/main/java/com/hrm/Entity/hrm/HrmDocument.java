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

/** Tài liệu đính kèm trong hồ sơ nhân viên. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_document", indexes = {
        @Index(name = "idx_hrm_doc_employee", columnList = "employee_id")
})
public class HrmDocument {
    @Id
    @Column(length = 40)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private HrmEmployee employee;

    private String name;
    private String type;
    private String size;
    private String updated;
    private String expires;
    private String url;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "content_type")
    private String contentType;

    public String getEmployeeId() {
        return employee == null ? null : employee.getId();
    }

    /** FE dùng cả hai field link/url. */
    public String getLink() {
        return url;
    }
}
