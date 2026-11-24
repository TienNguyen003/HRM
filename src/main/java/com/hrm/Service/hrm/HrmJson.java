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
        Map<String, Object> map = MAPPER.convertValue(entity, new TypeReference<LinkedHashMap<String, Object>>() {});
        if (entity instanceof HrmAttributesAware aware && aware.getAttributes() != null) {
            for (Map.Entry<String, Object> e : aware.getAttributes().entrySet()) {
                map.putIfAbsent(e.getKey(), e.getValue());
            }
        }
        return map;
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
