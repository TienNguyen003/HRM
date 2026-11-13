package com.hrm.dto.response.document;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentPreview {
    int id;
    String name;
    String type;
    String filePath;
    long size;
}
