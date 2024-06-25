package com.hrm.Entity.wage;

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
public class Advance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    LocalDateTime requestTime;
    LocalDateTime approvalTime;
    String money;
    String approvedBy;
    int status;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;
}
