package com.contractor.controller.IndustryTest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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
public class IndustryIntegrationTest {

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
    void createAndGetIndustryByIdTest() throws Exception {
        String insertIndustrySql = "INSERT INTO industry (id, name, is_active) VALUES (:id, :name, :active)";
        String selectIndustrySql = "SELECT * FROM industry WHERE id = :id";
        
        String json = """
        {
            "id": "51",
            "name": "rowing",
            "active": true
        }
        """;

        mockMvc.perform(put("/industry/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("51"))
            .andExpect(jsonPath("$.name").value("rowing"));

        mockMvc.perform(get("/industry/51"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("51"))
            .andExpect(jsonPath("$.name").value("rowing"));
    }

    @Test
    void getAllIndustryTest() throws Exception {
        String countIndustriesSql = "SELECT COUNT(*) FROM industry";
        int initialCount = jdbcTemplate.queryForObject(countIndustriesSql, Map.of(), Integer.class);
        
        mockMvc.perform(get("/industry/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(initialCount));
    }

    @Test
    void deleteIndustryTest() throws Exception {
        String insertContractorSql = """
            INSERT INTO contractor (id, parent_id, name, name_full, inn, ogrn, country, industry, org_form, is_active)
            VALUES (:id, NULL, :name, :nameFull, :inn, :ogrn, :country, :industry, :orgForm, true)
            """;
            
        String checkIndustrySql = "SELECT is_active FROM industry WHERE id = :id";
        String checkContractorSql = "SELECT is_active FROM contractor WHERE industry = :industry";

        jdbcTemplate.update(insertContractorSql, Map.of(
            "id", "id1",
            "name", "ilya",
            "nameFull", "ilya bobkov",
            "inn", "123",
            "ogrn", "12345",
            "country", "ABH",
            "industry", 51,
            "orgForm", 1
        ));

        mockMvc.perform(delete("/industry/delete/51"))
            .andExpect(status().isNoContent());

        Boolean isIndustryActive = jdbcTemplate.queryForObject(
            checkIndustrySql,
            Map.of("id", 51),
            Boolean.class
        );
        
        Boolean isContractorActive = jdbcTemplate.queryForObject(
            checkContractorSql,
            Map.of("industry", 51),
            Boolean.class
        );

        assertFalse(isIndustryActive);
        assertFalse(isContractorActive);
    }

}
