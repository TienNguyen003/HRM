package com.hrm.Service.hrm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Cấu hình khởi tạo dữ liệu cho module HRM (/api/hrm).
 *
 * - referenceEnabled: seed danh mục/dữ liệu tham chiếu (departments, leaveTypes, ...).
 *   Mặc định bật vì các filter/dropdown cần dữ liệu này để vận hành.
 * - sampleEnabled: seed dữ liệu nghiệp vụ mẫu (nhân viên, chấm công, ...).
 *   Mặc định TẮT. Chỉ bật ở môi trường dev/demo. Chạy đúng 1 lần nhờ seed log.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.hrm.seed")
public class HrmSeedProperties {
    /** Bật seed danh mục/reference. */
    private boolean referenceEnabled = true;

    /** Bật import dữ liệu mẫu vào DB. Mặc định tắt. */
    private boolean sampleEnabled = false;

    /** Khóa định danh lần import mẫu. Đổi khóa này nếu muốn import lại bộ mẫu mới. */
    private String sampleKey = "HRM-SAMPLE-2026-09";

    /** Khi true: chỉ import mẫu nếu các bảng nghiệp vụ còn trống. */
    private boolean sampleOnlyWhenEmpty = true;
}
