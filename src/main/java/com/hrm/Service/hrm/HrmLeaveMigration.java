package com.hrm.Service.hrm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Import đơn nghỉ cũ (application_leave) vào nguồn duy nhất hrm_leave_request. Idempotent. */
@Slf4j
@Component
@Order(41)
@RequiredArgsConstructor
public class HrmLeaveMigration implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!tableExists("application_leave") || !tableExists("hrm_leave_request")) return;
        Long done = jdbc.queryForObject(
                "select count(*) from hrm_leave_request where legacy_id is not null", Long.class);
        if (done != null && done > 0) return;

        int imported = jdbc.update(
                "insert into hrm_leave_request (id, legacy_id, employee_id, employee_name, type, start, end, days, "
                        + "status, approver, submitted, reason) "
                        + "select concat('LV-LEGACY-', a.id), a.id, e.id, e.name, "
                        + "coalesce(nullif(d.name_day, ''), 'Annual Leave'), "
                        + "left(a.start_time, 10), left(a.end_time, 10), "
                        + "greatest(1, cast(coalesce(regexp_substr(a.total_time, '[0-9]+(\\.[0-9]+)?'), '8') as decimal(10,2)) / 8), "
                        + "case a.status when 1 then 'Approved' when 2 then 'Rejected' when 3 then 'Cancelled' else 'Pending' end, "
                        + "coalesce(a.approved, ''), left(a.creation_time, 10), coalesce(a.reason, '') "
                        + "from application_leave a "
                        + "join employee e on e.id = a.employee_id "
                        + "left join day_off_categories d on d.id = a.day_off_categories_id");
        if (imported > 0) log.info("[HRM Migration] Import đơn nghỉ cũ: {} bản ghi", imported);
    }

    private boolean tableExists(String table) {
        Integer count = jdbc.queryForObject(
                "select count(*) from information_schema.tables where table_schema = database() and table_name = ?",
                Integer.class, table);
        return count != null && count > 0;
    }
}
