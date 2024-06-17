package com.hrm.Entity.day_off;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DayOffCategories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String nameDay;
    int timeDay;
    LocalDateTime timeUpdate;
    int status;
}
