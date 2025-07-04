package com.contractor.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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
public class ContractorIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        jdbcTemplate.update("DELETE FROM contractor");
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

        mockMvc.perform(put("/contractor/save").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andExpect(content().string("id1"));

        mockMvc.perform(get("/contractor/id1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("id1"))
            .andExpect(jsonPath("$.country.id").value("ABH"));
    }

    @Test
    void deleteContractorTest() throws Exception{

        jdbcTemplate.update("""
            INSERT INTO contractor (id, parent_id, name,
            name_full, inn, ogrn, country, industry, org_form)
            VALUES ('id2', NULL, 'ilya', 'ilya bobkov', '123', '12345', 'ABH', '1', '1')
            """ );

        mockMvc.perform(delete("/contractor/delete/id2"))
            .andExpect(status().isNoContent());

        Boolean isActive = jdbcTemplate.queryForObject(
            "SELECT is_active FROM contractor WHERE id = 'id2'", Boolean.class
        );

        assertFalse(isActive);
    }

    @Test
    void paginationGetAllContractorTest() throws Exception{
        for (int i = 0; i <= 10; i++){
            jdbcTemplate.update("INSERT INTO contractor (id, parent_id, name, " +
            " name_full, inn, ogrn, country, industry, org_form) " +
            " VALUES ('id" + i + "', NULL, 'ilya', 'ilya bobkov', '123', '12345', 'ABH', '1', '1')");
        }

        mockMvc.perform(get("/contractor/all/1/3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$.[0].id").value("id3"));
    }

    @Test
    void contractorPaginationFilterTest() throws Exception{

        jdbcTemplate.update("INSERT INTO contractor (id, parent_id, name, " +
            " name_full, inn, ogrn, country, industry, org_form) " +
            " VALUES ('id1', NULL, 'ilya', 'ilya bobkov', '123', '12345', 'ABH', '1', '1')");

        jdbcTemplate.update("INSERT INTO contractor (id, parent_id, name, " +
            " name_full, inn, ogrn, country, industry, org_form) " +
            " VALUES ('id2', NULL, 'ivan', 'ivan bobrov', '123', '12345', 'ABH', '1', '1')");

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

        String json2 = """
        {
            "nameFull": "bob"
        }
        """;

        mockMvc.perform(post("/contractor/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[1].nameFull").value("ivan bobrov"));
    }

}
