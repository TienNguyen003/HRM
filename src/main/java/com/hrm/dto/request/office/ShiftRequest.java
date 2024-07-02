package com.hrm.dto.request.office;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftRequest {
    Time start;
    Time end;
    String workingDay;
}
