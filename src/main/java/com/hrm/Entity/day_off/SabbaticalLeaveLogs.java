package com.hrm.Entity.day_off;

import com.hrm.Entity.user.Employee;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SabbaticalLeaveLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    LocalDateTime updateTime;
    int fluctuatesTime;
    String content;
    int remaining;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;
}
