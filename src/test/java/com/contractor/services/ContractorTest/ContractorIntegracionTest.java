package com.contractor.services.ContractorTest;

import static org.junit.Assert.*;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.contractor.dto.GetContactorByIdDto;
import com.contractor.dto.SaveContractorDto;
import com.contractor.services.ContractorServices;

@SpringBootTest
@Testcontainers
public class ContractorIntegracionTest {
    
    @Autowired
    ContractorServices contractorServices;

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Container
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.yaml");
        }

    @BeforeEach
    void init() {
        String deleteSql = "DELETE FROM contractor";
        jdbcTemplate.getJdbcTemplate().update(deleteSql);
    }

    @Test
    void saveAndGetContractorByIdTest() {
        SaveContractorDto testContractor = new SaveContractorDto();
        testContractor.setId("testId");
        testContractor.setParentId(null);
        testContractor.setName("ilya");
        testContractor.setNameFull("ilya bobkov");
        testContractor.setInn("12345");
        testContractor.setOgrn("123");
        testContractor.setCountry("ABH");
        testContractor.setIndustry(1);
        testContractor.setOrgForm(1);

        contractorServices.saveContractor(testContractor);
        GetContactorByIdDto result = contractorServices.getContractorById("testId");

        assertEquals(testContractor.getId(), result.getId());
    }

    @Test
    void deleteContractorById() {
        String insertSql = """
            INSERT INTO contractor
            (id, parent_id, name, name_full, inn, ogrn, country, industry, org_form, is_active)
            VALUES ('testId', null, 'test', 'test full', '123', '456', 'ABH', 1, 1, true)
            """;
        String checkSql = "SELECT is_active FROM contractor WHERE id = :id";

        jdbcTemplate.getJdbcTemplate().update(insertSql);
        contractorServices.deleteContractorById("testId");

        Boolean result = jdbcTemplate.queryForObject(
            checkSql,
            Map.of("id", "testId"),
            Boolean.class
        );
        assertFalse(result);
    }

    @Test
    void testAllPagination() {
        String insertSql = """
            INSERT INTO contractor
            (id, parent_id, name, name_full, inn, ogrn, country, industry, org_form)
            VALUES (:id, null, 'test', 'test full', '123', '456', 'ABH', 1, 1)
            """;

        for(int i=1; i<=7; i++) {
            jdbcTemplate.update(insertSql, Map.of("id", "testId" + i));
        }

        var page0 = contractorServices.getAllContractorPagination(0, 3);
        assertEquals(3, page0.size());
        assertEquals("testId1", page0.get(0).getId());

        var page1 = contractorServices.getAllContractorPagination(1, 3);
        assertEquals(3, page1.size());
        assertEquals("testId4", page1.get(0).getId());
    }

}
