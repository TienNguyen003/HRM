package com.hrm.Entity.wage;

import com.hrm.Entity.user.Bank;
import com.hrm.Entity.user.Employee;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String time;
    String advance;
    String totalSalary;
    int status;

    @ManyToOne
    Employee employee;

    @ManyToOne
    Bank bank;
}
