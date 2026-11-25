package com.hrm.Service.hrm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hrm.Entity.hrm.HrmAttributesAware;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Chuyển entity <-> Map theo đúng shape response của API /api/hrm. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HrmJson {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static Map<String, Object> toMap(Object entity) {
        if (entity == null) return null;
        if (entity instanceof com.hrm.Entity.user.Employee employee) {
            return employeeToMap(employee);
        }
        Map<String, Object> map = MAPPER.convertValue(entity, new TypeReference<LinkedHashMap<String, Object>>() {});
        if (entity instanceof HrmAttributesAware aware && aware.getAttributes() != null) {
            for (Map.Entry<String, Object> e : aware.getAttributes().entrySet()) {
                map.putIfAbsent(e.getKey(), e.getValue());
            }
        }
        return map;
    }

    /** Chuyển Employee sang đúng shape response của /api/hrm/employees. */
    private static Map<String, Object> employeeToMap(com.hrm.Entity.user.Employee e) {
        String code = displayCode(e);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", code);
        m.put("employeeId", code);
        m.put("employeeDbId", e.getId());
        m.put("firstName", nvl(e.getFirstName()));
        m.put("lastName", nvl(e.getLastName()));
        m.put("name", nvl(e.getName()));
        m.put("initials", nvl(e.getInitials()));
        m.put("position", nvl(e.getPosition()));
        m.put("department", nvl(e.getDepartmentName()));
        m.put("manager", nvl(e.getManager()));
        m.put("employmentType", nvl(e.getEmploymentType()));
        m.put("status", nvl(e.getStatus()));
        m.put("joinedDate", nvl(e.getJoined_date()));
        m.put("joinedDateIso", nvl(e.getJoinedDateIso()));
        m.put("email", nvl(e.getEmail()));
        m.put("phone", nvl(e.getPhone_number()));
        m.put("dateOfBirth", nvl(e.getBirth_date()));
        m.put("address", nvl(e.getAddress()));
        Map<String, Object> contact = new LinkedHashMap<>();
        contact.put("name", nvl(e.getEmergencyName()));
        contact.put("relation", nvl(e.getEmergencyRelation()));
        contact.put("phone", nvl(e.getEmergencyPhone()));
        m.put("emergencyContact", contact);
        m.put("location", nvl(e.getLocation()));
        m.put("idCard", nvl(e.getIdCard()));
        m.put("bankAccount", nvl(e.getBankAccount()));
        m.put("probationEndDate", nvl(e.getProbationEndDate()));
        m.put("avatar", nvl(e.getAvatar()));
        m.put("attendanceRate", e.getAttendanceRate());
        m.put("leaveBalance", e.getLeaveBalance());
        m.put("goals", e.getGoals());
        return m;
    }

    /** Mã EMP-00123; bản ghi cũ chưa có code thì suy từ id số. */
    public static String displayCode(com.hrm.Entity.user.Employee e) {
        return e.getHrmId();
    }

    private static String nvl(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    public static List<Map<String, Object>> toMaps(List<?> entities) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object e : entities) result.add(toMap(e));
        return result;
    }

    public static <T> T fromMap(Map<String, Object> map, Class<T> type) {
        return MAPPER.convertValue(map, type);
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }
}
