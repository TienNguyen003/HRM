package com.hrm.Entity.day_off;

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
public class ApplicationLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String startTime;
    String endTime;
    String totalTime;
    String approved;
    String reason;
    int status;
    String creationTime;

    @ManyToOne
    DayOffCategories dayOffCategories;

    @ManyToOne
    Employee employee;
}
