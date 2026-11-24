package com.hrm.hrm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Kiểm tra contract API /api/hrm vẫn giữ nguyên path và shape response. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HrmApiContractTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = {"ALL"})
    void getEmployees_returnsPaginatedContract() throws Exception {
        mockMvc.perform(get("/api/hrm/employees").param("page", "1").param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items").isArray())
                .andExpect(jsonPath("$.result.page").value(1))
                .andExpect(jsonPath("$.result.pageSize").value(5))
                .andExpect(jsonPath("$.result.total").exists())
                .andExpect(jsonPath("$.result.allItems").isArray())
                .andExpect(jsonPath("$.result.departments").isArray());
    }

    @Test
    @WithMockUser(authorities = {"ALL"})
    void crudEmployee_viaApi() throws Exception {
        Map<String, Object> payload = Map.of(
                "firstName", "Api",
                "lastName", "Tester",
                "email", "api@test.io",
                "department", "Engineering",
                "joinDate", "2026-02-01");

        String body = mockMvc.perform(post("/api/hrm/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.employeeId").exists())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).path("result").path("id").asText();

        mockMvc.perform(get("/api/hrm/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Api Tester"))
                .andExpect(jsonPath("$.result.attendance").isArray())
                .andExpect(jsonPath("$.result.balance").exists());

        mockMvc.perform(put("/api/hrm/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"position\":\"Senior QA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.position").value("Senior QA"));

        mockMvc.perform(delete("/api/hrm/employees/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/hrm/employees/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"ALL"})
    void getFilterOptions_readsFromReferenceSeed() throws Exception {
        mockMvc.perform(get("/api/hrm/filter-options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.departments").isArray())
                .andExpect(jsonPath("$.result.leaveTypes").isArray());
    }

    @Test
    void withoutAuthentication_isRejected() throws Exception {
        mockMvc.perform(get("/api/hrm/employees"))
                .andExpect(status().isUnauthorized());
    }
}
