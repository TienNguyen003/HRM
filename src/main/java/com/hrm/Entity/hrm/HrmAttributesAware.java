package com.hrm.Entity.hrm;

import java.util.Map;

/** Cho phép lưu các field mở rộng của payload mà không làm mất dữ liệu khi chuyển entity <-> Map. */
public interface HrmAttributesAware {
    Map<String, Object> getAttributes();

    void setAttributes(Map<String, Object> attributes);
}
