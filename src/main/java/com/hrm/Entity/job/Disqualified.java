package com.hrm.Entity.job;

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
public class Disqualified {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String email;
    String dob;
    String phone;
    String linkFile;
    int status;
    LocalDateTime updateTime;

    // pipeline tuyển dụng
    Integer jobId;      // tin tuyển dụng đang theo (nullable)
    int stage = 0;      // 0 mới, 1 sàng lọc, 2 phỏng vấn, 3 tuyển, 4 loại
    String interviewTime;
}
