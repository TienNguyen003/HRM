package com.hrm.Entity.wage;

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
public class Wage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String salary;

    @ManyToOne
    WageCategories wageCategories;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;
}
