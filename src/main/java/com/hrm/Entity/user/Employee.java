package com.hrm.Entity.user;

import com.hrm.Entity.department.Department;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    String email;
    String phone_number;
    String gender;
    String image;
    Date birth_date;
    Date hire_date;
    int shift_id;
    int vacationTime;

    @ManyToOne
    Department department;
}
