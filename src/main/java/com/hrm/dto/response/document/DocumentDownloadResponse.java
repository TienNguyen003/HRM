package com.hrm.dto.response.document;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentDownloadResponse {
    int id;
    String name;
    String filePath;
    long size;
}
