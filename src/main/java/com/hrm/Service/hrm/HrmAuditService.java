package com.hrm.Service.hrm;

import com.hrm.Entity.hrm.HrmAuditLog;
import com.hrm.repository.hrm.HrmAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/** Ghi audit log cho các thao tác nghiệp vụ quan trọng. */
@Service
@RequiredArgsConstructor
public class HrmAuditService {
    private final HrmAuditLogRepository auditLogRepository;
    private final AtomicLong sequence = new AtomicLong();

    @Transactional
    public void record(String action, String entity, String entityId, String actor) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String id = String.format("AUD-%d-%06d", System.currentTimeMillis() % 1_000_000, sequence.incrementAndGet() % 1_000_000);
        auditLogRepository.save(new HrmAuditLog(id, action, entity, entityId, actor, ts));
    }

    public List<Map<String, Object>> findAll() {
        return HrmJson.toMaps(auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "date")));
    }
}
