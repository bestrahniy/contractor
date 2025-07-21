package com.contractor.controller.ContractorTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Map;
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
public class ContractorIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Container
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.change-log", () ->  "classpath:db/changelog/db.changelog-master.yaml");
    }

    @BeforeEach
    void init(){
        jdbcTemplate.update("DELETE FROM contractor", Map.of());
    }

    @Test
    void saveAndGetByIdTest() throws Exception{
        String json = """
        {
            "id": "id1",
            "parentId": null,
            "name": "ilya",
            "nameFull": "ilya bobkov",
            "inn": "12345678",
            "ogrn": "1234567890",
            "country": "ABH",
            "industry": 1,
            "orgForm": 1
        }
        """;

        mockMvc.perform(put("/contractor/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/contractor/id1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("id1"))
            .andExpect(jsonPath("$.country.id").value("ABH"));
    }

    @Test
    void deleteContractorTest() throws Exception{

        jdbcTemplate.update("""
            INSERT INTO contractor (id, parent_id, name, name_full,
            inn, ogrn, country, industry, org_form, is_active)
            VALUES (:id, NULL, :name, :nameFull, :inn, :ogrn, :country, :industry, :orgForm, true)
            """,
            Map.of(
                "id", "id2",
                "name", "ilya",
                "nameFull", "ilya bobkov",
                "inn", "123",
                "ogrn", "12345",
                "country", "ABH",
                "industry", 1,
                "orgForm", 1
            )
        );

        mockMvc.perform(delete("/contractor/delete/id2"))
            .andExpect(status().isNoContent());

        Boolean isActive = jdbcTemplate.queryForObject(
            "SELECT is_active FROM contractor WHERE id = :id",
            Map.of("id", "id2"),
            Boolean.class
        );

        assertFalse(isActive);
    }

    @Test
    void paginationGetAllContractorTest() throws Exception {
        for (int i = 0; i <= 10; i++) {
            Map<String, Object> params = Map.of(
                "id", "id" + i,
                "name", "ilya",
                "name_full", "ilya bobkov",
                "inn", "123",
                "ogrn", "12345",
                "country", "ABH",
                "industry", 1,
                "org_form", 1
            );
            String sql = """
                INSERT INTO contractor (id, parent_id, name,
                name_full, inn, ogrn, country, industry, org_form)
                VALUES (:id, NULL, :name, :name_full, :inn, :ogrn, :country, :industry, :org_form)
                """;
            jdbcTemplate.update(sql, params);
        }

        mockMvc.perform(get("/contractor/all/1/3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$.[0].id").value("id3"));
    }

    @Test
    void contractorFilterTest() throws Exception{
        String sql1 = """
            INSERT INTO contractor (id, parent_id, name, name_full,
            inn, ogrn, country, industry, org_form)
            VALUES (:id1, NULL, :name1, :name_full1, :inn, :ogrn, :country, :industry, :orgForm)
                """;
        Map<String, Object> params1 = Map.of(
                "id1", "id1",
                "name1", "ilya",
                "name_full1", "ilya bobkov",
                "inn", "123",
                "ogrn", "12345",
                "country", "ABH",
                "industry", 1,
                "orgForm", 1
        );
        jdbcTemplate.update(sql1, params1);

        String sql2 = """
            INSERT INTO contractor (id, parent_id, name, name_full,
            inn, ogrn, country, industry, org_form)
            VALUES (:id2, NULL, :name2, :name_full2, :inn, :ogrn, :country, :industry, :orgForm)
                """;
        Map<String, Object> params2= Map.of(
                "id2", "id2",
                "name2", "ivan",
                "name_full2", "ivan bobrov",
                "inn", "123",
                "ogrn", "12345",
                "country", "ABH",
                "industry", 1,
                "orgForm", 1
        );
        jdbcTemplate.update(sql2, params2);

        String json = """
        {
            "id": "id1"
        }
        """;

        mockMvc.perform(post("/contractor/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("id1"));
    }

}
