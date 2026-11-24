package com.hrm.Entity.hrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Điểm dữ liệu tăng trưởng nhân sự theo tháng. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hrm_growth")
public class HrmGrowth {
    @Id
    @Column(length = 40)
    private String id;
    private String month;
    private int total;
    private int hires;
    private int exits;

    /** id là khóa kỹ thuật, không trả về FE (seed không có field này). */
    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
