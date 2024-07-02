package com.hrm.dto.response.office;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftResponse {
    int id;
    Time start;
    Time end;
    String workingDay;
}
