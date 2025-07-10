package com.contractor.controller.OrgFormTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private JdbcTemplate jdbcTemplate;

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
    void saveAndGetOrgFormById() throws Exception{
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
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.id").value("1"));

        mockMvc.perform(get("/orgform/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("чтото"));
    }

    @Test
    void getAllOrgFormTest() throws Exception {
        mockMvc.perform(get("/orgform/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(150));
    }

    @Test
    void deleteOrgFormTest() throws Exception {
        jdbcTemplate.update("INSERT INTO contractor (id, parent_id, name, " +
            " name_full, inn, ogrn, country, industry, org_form) " +
            " VALUES ('id1', NULL, 'ilya', 'ilya bobkov', '123', '12345', 'ABH', '51', '51')");

        mockMvc.perform(delete("/orgform/delete/51"))
            .andExpect(status().is2xxSuccessful());

        Boolean testResult1 = jdbcTemplate.queryForObject(
            "SELECT is_active FROM org_form WHERE id = '51'",
            Boolean.class);

        Boolean testResult2 = jdbcTemplate.queryForObject(
        "SELECT is_active FROM contractor WHERE org_form = '51'",
        Boolean.class);

        assertFalse(testResult1);
        assertFalse(testResult2);
    }
}
