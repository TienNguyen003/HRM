package com.hrm.dto.response.job;

import com.hrm.Entity.job.Assessment;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequirementsResponse {
    int id;
    String title;

    int assessmentId;
}
