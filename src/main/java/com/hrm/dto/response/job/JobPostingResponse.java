package com.hrm.dto.response.job;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobPostingResponse {
    int id;
    String title;
    String description;
    int quantity;
    String salaryRange;
    String requirements;
    int status;
    String createTime;
}
