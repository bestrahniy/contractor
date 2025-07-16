package com.contractor.controller.OrgFormTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class OrgFormIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Container
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registrar){
        registrar.add("spring.datasource.url", postgres::getJdbcUrl);
        registrar.add("spring.datasource.username", postgres::getUsername);
        registrar.add("spring.datasource.password", postgres::getPassword);
        registrar.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.yaml");
    }

    @Test
    void saveAndGetOrgFormById() throws Exception {
        String insertOrgFormSql = "INSERT INTO org_form (id, name, is_active) VALUES (:id, :name, :active)";
        String selectOrgFormSql = "SELECT * FROM org_form WHERE id = :id";
        
        String json = """
        {
            "id": "1",
            "name": "чтото",
            "active": true
        }
        """;

        mockMvc.perform(put("/orgform/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("чтото"));

        mockMvc.perform(get("/orgform/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("чтото"));
    }

    @Test
    void getAllOrgFormTest() throws Exception {
        String countOrgFormsSql = "SELECT COUNT(*) FROM org_form";
        
        int currentCount = jdbcTemplate.queryForObject(countOrgFormsSql, Map.of(), Integer.class);
        
        mockMvc.perform(get("/orgform/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(currentCount));
    }

    @Test
    void deleteOrgFormTest() throws Exception {
        String insertContractorSql = """
            INSERT INTO contractor (id, parent_id, name, name_full, inn, ogrn, country, industry, org_form, is_active)
            VALUES (:id, NULL, :name, :nameFull, :inn, :ogrn, :country, :industry, :orgForm, true)
            """;
        
        String checkOrgFormSql = "SELECT is_active FROM org_form WHERE id = :id";
        String checkContractorSql = "SELECT is_active FROM contractor WHERE org_form = :orgForm";

        jdbcTemplate.update(insertContractorSql, Map.of(
            "id", "id1",
            "name", "ilya",
            "nameFull", "ilya bobkov",
            "inn", "123",
            "ogrn", "12345",
            "country", "ABH",
            "industry", 51,
            "orgForm", 51
        ));

        mockMvc.perform(delete("/orgform/delete/51"))
            .andExpect(status().isNoContent());

        Boolean isOrgFormActive = jdbcTemplate.queryForObject(
            checkOrgFormSql,
            Map.of("id", 51),
            Boolean.class
        );
        
        Boolean isContractorActive = jdbcTemplate.queryForObject(
            checkContractorSql,
            Map.of("orgForm", 51),
            Boolean.class
        );

        assertFalse(isOrgFormActive);
        assertFalse(isContractorActive);
    }
}
