package com.hrm.Entity.office;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    /** Mã SH-001 dùng bởi /api/hrm/shift-schedules. */
    @Column(name = "shift_code", length = 40)
    private String code;

    @Column(name = "shift_name")
    private String name;

    Time start;
    Time end;
    String workingDay;

    @Column(name = "break_minutes")
    Integer breakMinutes;
    String location;
}
