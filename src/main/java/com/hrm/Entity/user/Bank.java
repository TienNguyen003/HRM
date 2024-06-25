package com.hrm.Entity.user;

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
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String nameBank;
    String owner;
    String numberBank;
    int priority;
    int status;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;
}
