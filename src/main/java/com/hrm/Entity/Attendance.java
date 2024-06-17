package com.hrm.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String username;
    int employee_id;
    String department_id;
    int shift_id;
    int location_id;
    int in_time;
    String notes;
    String image;
    String lack_of;
    String in_status;
    int out_time;
    int out_status;
}
