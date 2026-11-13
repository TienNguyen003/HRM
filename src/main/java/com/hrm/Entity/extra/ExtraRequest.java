package com.hrm.Entity.extra;

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
public class ExtraRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int type;           // 1 tăng ca, 2 công tác, 3 làm việc từ xa
    String date;        // yyyy-MM-dd (ngày tăng ca / từ ngày)
    String endDate;     // yyyy-MM-dd (đến ngày,nullable)
    double hours;       // số giờ (tăng ca)
    String place;       // địa điểm (công tác)
    String reason;
    int status = 0;     // 0 chờ, 1 duyệt, 2 từ chối
    String approvedBy;
    String creationTime;

    @ManyToOne
    Employee employee;
}