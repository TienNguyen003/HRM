package com.hrm.Service.role;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Chuẩn hóa permission về dạng domain-action.
 *
 * Các tên legacy vẫn được giữ trong DB/token để tương thích ngược, nhưng mọi phép
 * kiểm tra đều đi qua {@link #canonical(String)}: "SALA_VIEW" và "PAYROLL_VIEW"
 * cùng có nghĩa là một quyền. Không còn tra DB lúc check permission.
 */
public final class PermissionCatalog {

    /** Quyền super-admin duy nhất. Không hardcode theo tên role. */
    public static final String SUPER = "ALL";

    private static final Map<String, String> LEGACY = new LinkedHashMap<>();

    static {
        // Giữ nguyên các permission domain-action đã có (identity).
        List.of(
                "SELF_VIEW", "SELF_UPDATE", "TEAM_VIEW",
                "EMP_VIEW", "EMP_ADD", "EMP_EDIT", "EMP_DELETE",
                "ATT_VIEW", "ATT_CHECKIN", "ATT_MANAGE",
                "LEAVE_VIEW", "LEAVE_CREATE", "LEAVE_APPROVE",
                "ORG_VIEW", "ORG_MANAGE",
                "REC_VIEW", "REC_ADD", "REC_EDIT", "REC_DELETE",
                "PERF_VIEW", "PERF_ADD", "PERF_EDIT",
                "USER_VIEW", "USER_ADD", "USER_EDIT", "USER_DELETE",
                "PERM_VIEW", "PERM_ADD", "PERM_MANAGE",
                "OT_VIEW", "OT_CREATE", "OT_APPROVE",
                "PAYROLL_VIEW", "PAYROLL_RUN", "PAYSLIP_VIEW",
                "ONBOARDING_VIEW", "ONBOARDING_MANAGE",
                "LEAVE_POLICY_VIEW", "LEAVE_POLICY_MANAGE",
                "SHIFT_VIEW", "SHIFT_MANAGE",
                "BENEFIT_VIEW", "BENEFIT_MANAGE",
                "HR_REQUEST_VIEW", "HR_REQUEST_CREATE", "HR_REQUEST_MANAGE",
                "ADV_VIEW", "ADV_MANAGE",
                "CV_VIEW", "CV_ADD", "CV_EDIT", "CV_DELETE",
                "TASK_VIEW", "TASK_MANAGE",
                "FILE_MANAGE", "REPORT_VIEW",
                "SETTINGS_VIEW", "SETTINGS_MANAGE",
                SUPER
        ).forEach(name -> LEGACY.put(name, name));

        // Role ADMIN / quyền ALL từ token cũ -> quyền super.
        LEGACY.put("ROLE_ADMIN", SUPER);
        LEGACY.put("ADMIN", SUPER);

        // Quản trị user/role.
        LEGACY.put("PERM_EDIT", "PERM_MANAGE");
        LEGACY.put("PERM_DELETE", "PERM_MANAGE");
        LEGACY.put("USER_RSPASS", "USER_EDIT");
        LEGACY.put("USER_CPASS", "SELF_UPDATE");

        // Lương: mọi module lương legacy gom về PAYROLL_VIEW / PAYROLL_RUN.
        legacySalary("SALA", "SAUP", "SAFI", "CATG", "CALC");
        LEGACY.put("PERF_ADD", "PERF_EDIT");

        // Nghỉ phép / ngày nghỉ / lễ.
        LEGACY.put("REQ_VIEW", "LEAVE_VIEW");
        LEGACY.put("HIST_VIEW", "LEAVE_VIEW");
        LEGACY.put("REQ_ADD", "LEAVE_CREATE");
        LEGACY.put("REQ_EDIT", "LEAVE_CREATE");
        LEGACY.put("REQ_DELETE", "LEAVE_CREATE");
        LEGACY.put("REQ_APPROVALS", "LEAVE_APPROVE");
        LEGACY.put("LEAV_VIEW", "LEAVE_POLICY_VIEW");
        LEGACY.put("LEAV_ADD", "LEAVE_POLICY_MANAGE");
        LEGACY.put("LEAV_EDIT", "LEAVE_POLICY_MANAGE");
        LEGACY.put("LEAV_DELETE", "LEAVE_POLICY_MANAGE");
        LEGACY.put("HOLI_VIEW", "LEAVE_POLICY_VIEW");
        LEGACY.put("HOLI_ADD", "LEAVE_POLICY_MANAGE");
        LEGACY.put("HOLI_EDIT", "LEAVE_POLICY_MANAGE");
        LEGACY.put("HOLI_DELETE", "LEAVE_POLICY_MANAGE");

        // Chấm công legacy.
        LEGACY.put("ATTD_VIEW", "ATT_VIEW");
        LEGACY.put("ATTD_ADD", "ATT_MANAGE");
        LEGACY.put("ATTD_EDIT", "ATT_MANAGE");
        LEGACY.put("ATTD_DELETE", "ATT_MANAGE");

        // Hồ sơ, hợp đồng, ngân hàng, tổ chức/văn phòng thuộc domain nhân sự.
        LEGACY.put("CONT_VIEW", "EMP_VIEW");
        LEGACY.put("CONT_ADD", "EMP_EDIT");
        LEGACY.put("CONT_EDIT", "EMP_EDIT");
        LEGACY.put("CONT_DELETE", "EMP_EDIT");
        LEGACY.put("BANK_VIEW", "EMP_VIEW");
        LEGACY.put("BANK_ADD", "EMP_EDIT");
        LEGACY.put("BANK_EDIT", "EMP_EDIT");
        LEGACY.put("BANK_DELETE", "EMP_EDIT");
        LEGACY.put("COMP_VIEW", "ORG_VIEW");
        LEGACY.put("COMP_ADD", "ORG_MANAGE");
        LEGACY.put("COMP_EDIT", "ORG_MANAGE");
        LEGACY.put("COMP_DELETE", "ORG_MANAGE");
        LEGACY.put("OFF_VIEW", "ORG_VIEW");
        LEGACY.put("OFF_ADD", "ORG_MANAGE");
        LEGACY.put("OFF_EDIT", "ORG_MANAGE");
        LEGACY.put("OFF_DELETE", "ORG_MANAGE");

        // Ứng lương.
        LEGACY.put("ADV_ADD", "ADV_MANAGE");
        LEGACY.put("ADV_EDIT", "ADV_MANAGE");
        LEGACY.put("ADV_DELETE", "ADV_MANAGE");
        LEGACY.put("ADV_APPROVALS", "ADV_MANAGE");
    }

    private static void legacySalary(String... prefixes) {
        for (String prefix : prefixes) {
            LEGACY.put(prefix + "_VIEW", "PAYROLL_VIEW");
            LEGACY.put(prefix + "_ADD", "PAYROLL_RUN");
            LEGACY.put(prefix + "_EDIT", "PAYROLL_RUN");
            LEGACY.put(prefix + "_DELETE", "PAYROLL_RUN");
            LEGACY.put(prefix + "_APPROVALS", "PAYROLL_RUN");
        }
    }

    private PermissionCatalog() {
    }

    /** Trả về quyền canonical; tên không được ánh xạ thì giữ nguyên. */
    public static String canonical(String name) {
        if (name == null || name.isBlank()) return "";
        String key = name.trim();
        return LEGACY.getOrDefault(key, key);
    }

    public static boolean isSuper(String name) {
        return SUPER.equals(canonical(name));
    }

    /** Danh sách mọi quyền canonical (dùng để seed và kiểm tra cấu hình). */
    public static Set<String> canonicalNames() {
        return Set.copyOf(LEGACY.values());
    }

    /** Cả tên canonical lẫn tên legacy, để DB vẫn giữ được role cũ. */
    public static Set<String> allSeedNames() {
        Set<String> names = new java.util.LinkedHashSet<>(LEGACY.keySet());
        names.addAll(LEGACY.values());
        names.remove("ROLE_ADMIN"); // role marker, không phải permission
        names.remove("ADMIN");      // role name, không phải permission
        return names;
    }
}
