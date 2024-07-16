package com.hrm.Entity.user;

import com.hrm.Entity.office.Department;
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
    String birth_date;
    String hire_date;
    int shift_id;
    int vacationTime;
    int hourOff;
    int vacationHours;

    @ManyToOne
    Department department;
}
