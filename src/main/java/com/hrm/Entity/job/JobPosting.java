package com.hrm.Entity.job;

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
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String title;       // chức danh tuyển dụng
    String description; // mô tả công việc
    int quantity;       // số lượng cần tuyển
    String salaryRange; // mức lương đề xuất (text)
    String requirements;// yêu cầu chung
    int status = 0;     // 0 đang tuyển, 1 đã đóng
    String createTime;
}