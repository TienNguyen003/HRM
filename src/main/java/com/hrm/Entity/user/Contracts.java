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
public class Contracts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String urlFile;
    String linkFile;
    String hire_date;
    String dismissal_date;
    int status;

    @OneToOne
    Employee employee;
}
