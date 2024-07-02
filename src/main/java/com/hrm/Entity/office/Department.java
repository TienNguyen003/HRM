package com.hrm.Entity.office;

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
public class Department {
    @Id
    String id;
    String name;
    String shortName;
    String belongTo;
    int status;

    @ManyToOne
    OfficeI officeI;
}
