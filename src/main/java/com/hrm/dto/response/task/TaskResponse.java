package com.hrm.dto.response.task;

import com.hrm.enums.TaskStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponse {
    int id;
    String title;
    String description;
    int priority;
    TaskStatus status;
    int employeeId;
    String employeeName;
    String creationTime;
    String dueDate;
}
