package com.hrm.Service.hrm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Đưa Department/Shift thành nguồn đơn nhất; idempotent. */
@Slf4j
@Component
@Order(42)
@RequiredArgsConstructor
public class HrmOrganizationMigration implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        syncOffices();
        syncDepartments();
        migrateShifts();
    }

    private boolean tableExists(String table) {
        Integer count = jdbc.queryForObject(
                "select count(*) from information_schema.tables where table_schema = database() and table_name = ?",
                Integer.class, table);
        return count != null && count > 0;
    }

    /** Danh mục locations trong seed trở thành OfficeI — nguồn duy nhất cho location. */
    private void syncOffices() {
        if (!tableExists("officei")) return;
        if (tableExists("hrm_reference_data")) {
            jdbc.update("insert into officei (name, status) "
                    + "select r.code, 1 from hrm_reference_data r "
                    + "where r.category = 'locations' "
                    + "and not exists (select 1 from officei o where o.name = r.code)");
        }
        jdbc.update("insert into officei (name, status) "
                + "select distinct e.location, 1 from employee e "
                + "where e.location is not null and e.location <> '' "
                + "and not exists (select 1 from officei o where o.name = e.location)");
        int linked = jdbc.update("update employee e join officei o on e.location = o.name "
                + "set e.office_id = o.id where e.office_id is null");
        if (linked > 0) log.info("[HRM Migration] Gắn Employee → OfficeI: {} bản ghi", linked);
    }

    private void syncDepartments() {
        if (!tableExists("department")) return;
        if (tableExists("hrm_reference_data")) {
            jdbc.update("insert into department (name, short_name, status) "
                    + "select r.code, r.code, 1 from hrm_reference_data r "
                    + "where r.category = 'departments' "
                    + "and not exists (select 1 from department d where d.name = r.code)");
        }
        jdbc.update("insert into department (name, short_name, status) "
                + "select distinct e.department_name, e.department_name, 1 from employee e "
                + "where e.department_name is not null and e.department_name <> '' "
                + "and not exists (select 1 from department d where d.name = e.department_name)");
        int linked = jdbc.update("update employee e join department d on e.department_name = d.name "
                + "set e.department_id = d.id where e.department_id is null");
        if (linked > 0) log.info("[HRM Migration] Gắn Employee → Department: {} bản ghi", linked);
    }

    private void migrateShifts() {
        if (!tableExists("hrm_shift_schedule") || !tableExists("shift")) return;
        Integer before = jdbc.queryForObject(
                "select count(*) from shift where shift_code is not null", Integer.class);
        if (before != null && before > 0) return;

        int moved = jdbc.update("insert into shift (shift_code, shift_name, start, end, working_day, break_minutes, location) "
                + "select s.id, s.name, cast(s.start_time as time), cast(s.end_time as time), 'Daily', "
                + "coalesce(s.break_minutes, 0), s.location from hrm_shift_schedule s "
                + "where not exists (select 1 from shift sh where sh.shift_code = s.id)");
        if (moved > 0) log.info("[HRM Migration] Gộp ca làm việc: {} bản ghi", moved);
    }
}
