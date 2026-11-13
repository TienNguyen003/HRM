package com.hrm.dto.request.document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentUploadRequest {
    @NotBlank(message = "Tên tài liệu không được để trống")
    String name;

    @NotBlank(message = "Loại tài liệu không được để trống")
    String type;

    @NotBlank(message = "Đường dẫn tài liệu không được để trống")
    String filePath;

    @Min(value = 1, message = "Dung lượng tài liệu phải lớn hơn 0")
    long size;

    String description;

    @NotBlank(message = "Người tải lên không được để trống")
    String uploadedBy;
}
