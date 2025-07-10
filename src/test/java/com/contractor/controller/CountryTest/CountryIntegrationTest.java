package com.contractor.controller.CountryTest;

import static org.junit.Assert.*;
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
public class CountryIntegrationTest {

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
    void saveAndGetCountryTest() throws Exception {
        String json = """
        {
            "id": "RU",
            "name": "Россия",
            "active": true
        }
        """;

        mockMvc.perform(put("/country/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.id").value("RU"));

        mockMvc.perform(get("/country/RU"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Россия"));
    }

    @Test
    void getAllCountryTest() throws Exception {
        mockMvc.perform(get("/country/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(254));
    }

    @Test
    void deletCountryTest() throws Exception {
        jdbcTemplate.update("INSERT INTO contractor (id, parent_id, name, " +
            " name_full, inn, ogrn, country, industry, org_form) " +
            " VALUES ('id1', NULL, 'ilya', 'ilya bobkov', '123', '12345', 'ABH', '1', '1')");

        mockMvc.perform(delete("/country/delete/ABH"))
            .andExpect(status().is2xxSuccessful());

        Boolean testResult = jdbcTemplate.queryForObject(
            "SELECT is_active FROM country WHERE id = 'ABH'",
            Boolean.class);

        Boolean testResultContractor = jdbcTemplate.queryForObject(
            "SELECT is_active FROM contractor WHERE country = 'ABH'",
            Boolean.class);

        assertFalse(testResult);
        assertFalse(testResultContractor);
    }
}
