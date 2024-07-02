package com.hrm.Entity.wage;

import com.hrm.Entity.office.Department;
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
    String salary;
    String advance;
    String totalSalary;
    int status;
    String qrPay;

    @OneToOne
    Employee employee;

    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department;
}
