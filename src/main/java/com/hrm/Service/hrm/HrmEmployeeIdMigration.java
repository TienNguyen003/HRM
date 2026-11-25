package com.hrm.Service.hrm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Migration gom 2 bảng nhân viên thành 1 (chạy TRƯỚC HrmSeeder).
 *
 * - Backfill employee_code (EMP-00123) cho bản ghi cũ trong bảng employee.
 * - Import bản ghi từ bảng hrm_employee cũ sang employee nếu chưa có.
 * - Đổi employee_id của các bảng hrm_* từ varchar(EMP-...) sang int (FK tới employee.id).
 *
 * Idempotent: bỏ qua nếu cột đã là int; dùng INSERT IGNORE/DROP FOREIGN KEY IF EXISTS.
 */
@Slf4j
@Component
@Order(40)
@RequiredArgsConstructor
public class HrmEmployeeIdMigration implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    private static final List<String> CHILD_TABLES = List.of(
            "hrm_attendance", "hrm_document", "hrm_leave_balance", "hrm_leave_request",
            "hrm_overtime_request", "hrm_payslip", "hrm_onboarding_task", "hrm_offboarding_task");

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!tableExists("employee")) return;

        ensureEmployeeCodeColumn();
        backfillEmployeeDefaults();
        backfillEmployeeCodes();
        if (tableExists("hrm_employee")) importLegacyEmployees();

        for (String table : CHILD_TABLES) {
            if (!tableExists(table)) continue;
            migrateChildTable(table);
        }
    }

    private boolean tableExists(String table) {
        Integer count = jdbc.queryForObject(
                "select count(*) from information_schema.tables where table_schema = database() and table_name = ?",
                Integer.class, table);
        return count != null && count > 0;
    }

    private boolean columnExists(String table, String column) {
        Integer count = jdbc.queryForObject(
                "select count(*) from information_schema.columns where table_schema = database() and table_name = ? and column_name = ?",
                Integer.class, table, column);
        return count != null && count > 0;
    }

    private String columnType(String table, String column) {
        try {
            String type = jdbc.queryForObject(
                    "select data_type from information_schema.columns where table_schema = database() and table_name = ? and column_name = ?",
                    String.class, table, column);
            return type == null ? "" : type.toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }

    private void ensureEmployeeCodeColumn() {
        if (columnExists("employee", "employee_code")) return;
        jdbc.execute("alter table employee add column employee_code varchar(20) null");
        jdbc.execute("create unique index uk_employee_code on employee(employee_code)");
        log.info("[HRM Migration] Đã thêm cột employee_code");
    }

    /** Cột primitive mới do Hibernate thêm có giá trị NULL trên bản ghi cũ. */
    private void backfillEmployeeDefaults() {
        int total = 0;
        total += jdbc.update("update employee set attendance_rate = 0 where attendance_rate is null");
        total += jdbc.update("update employee set leave_balance = 20 where leave_balance is null");
        total += jdbc.update("update employee set goals = 0 where goals is null");
        total += jdbc.update("update employee set timekeeping = 0 where timekeeping is null");
        total += jdbc.update("update employee set lateness = 0 where lateness is null");
        total += jdbc.update("update employee set shift_id = 0 where shift_id is null");
        total += jdbc.update("update employee set vacation_time = 0 where vacation_time is null");
        total += jdbc.update("update employee set hour_off = 0 where hour_off is null");
        total += jdbc.update("update employee set vacation_hours = 0 where vacation_hours is null");
        if (total > 0) log.info("[HRM Migration] Backfill giá trị mặc định: {} ô", total);
    }

    private void backfillEmployeeCodes() {
        int updated = jdbc.update("update employee set employee_code = concat('EMP-', lpad(id, 5, '0')) "
                + "where employee_code is null or employee_code = ''");
        if (updated > 0) log.info("[HRM Migration] Backfill employee_code: {} bản ghi", updated);
    }

    /** Import nhân viên từ bảng hrm_employee cũ (nếu còn) sang bảng employee. */
    private void importLegacyEmployees() {
        int imported = jdbc.update("insert ignore into employee (employee_code, name, first_name, last_name, initials, "
                + "position, department_name, manager, employment_type, status, joined_date, joined_date_iso, "
                + "email, phone_number, birth_date, address, emergency_name, emergency_relation, emergency_phone, "
                + "location, id_card, bank_account, probation_end_date, avatar, attendance_rate, leave_balance, goals) "
                + "select id, name, first_name, last_name, initials, position, department, manager, employment_type, status, "
                + "joined_date, joined_date_iso, email, phone, date_of_birth, address, emergency_name, emergency_relation, "
                + "emergency_phone, location, id_card, bank_account, probation_end_date, avatar, attendance_rate, leave_balance, goals "
                + "from hrm_employee where id is not null");
        if (imported > 0) log.info("[HRM Migration] Import từ hrm_employee: {} bản ghi", imported);
    }

    private void migrateChildTable(String table) {
        String type = columnType(table, "employee_id");
        log.info("[HRM Migration] {} employee_id type={}", table, type);
        if (type.contains("int")) return; // đã migration

        // Xóa các bản ghi mồ côi (không map được sang employee).
        int orphans = jdbc.update("delete from " + table + " t where t.employee_id is not null "
                + "and t.employee_id not regexp '^[0-9]+$' "
                + "and not exists (select 1 from employee e where e.employee_code = t.employee_id)");
        if (orphans > 0) log.warn("[HRM Migration] {} bỏ qua bản ghi mồ côi tại {}", orphans, table);

        // FK cũ trỏ tới hrm_employee phải bỏ trước khi đổi giá trị employee_id.
        dropForeignKeys(table);

        // Đổi EMP-00123 thành id số tương ứng (lưu dạng chuỗi để chuẩn bị ALTER).
        jdbc.update("update " + table + " t join employee e on t.employee_id = e.employee_code "
                + "set t.employee_id = cast(e.id as char) where t.employee_id not regexp '^[0-9]+$'");

        jdbc.execute("alter table " + table + " modify column employee_id int not null");
        jdbc.execute("alter table " + table + " add constraint fk_" + table + "_employee "
                + "foreign key (employee_id) references employee(id)");
        log.info("[HRM Migration] {} → employee_id int", table);
    }

    private void dropForeignKeys(String table) {
        List<String> names = jdbc.queryForList(
                "select constraint_name from information_schema.key_column_usage "
                        + "where table_schema = database() and table_name = ? and column_name = 'employee_id' "
                        + "and referenced_table_name is not null",
                String.class, table);
        for (String name : names) {
            jdbc.execute("alter table " + table + " drop foreign key `" + name + "`");
        }
    }
}
