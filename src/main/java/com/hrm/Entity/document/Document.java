package com.hrm.Entity.document;

import com.hrm.enums.DocumentStatus;
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
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;            // tên tài liệu
    String type;            // loại tài liệu: contract, cv, ...
    String filePath;        // đường dẫn / url tài liệu
    long size;              // dung lượng (byte)
    String description;
    String uploadedBy;      // tên đăng nhập người tải lên
    @Enumerated(EnumType.STRING)
    @Builder.Default
    DocumentStatus status = DocumentStatus.PENDING_UPLOAD;
    String creationTime;
}

