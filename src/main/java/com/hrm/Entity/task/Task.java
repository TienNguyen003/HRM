package com.hrm.Entity.task;

import com.hrm.Entity.user.Employee;
import com.hrm.enums.TaskStatus;
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
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String title;               // tên nhiệm vụ
    String description;
    int priority;               // độ ưu tiên (1 = cao nhất)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    TaskStatus status = TaskStatus.PENDING;
    String creationTime;        // thời điểm giao việc
    String dueDate;             // yyyy-MM-dd

    @ManyToOne
    Employee employee;          // nhân viên được giao việc
}
