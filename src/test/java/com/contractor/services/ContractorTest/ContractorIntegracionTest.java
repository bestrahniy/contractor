package com.contractor.services.ContractorTest;


import static org.junit.Assert.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.contractor.dto.GetContactorByIdDto;
import com.contractor.dto.SaveContractorDto;
import com.contractor.dto.SearchContractorRequestDto;
import com.contractor.services.ContractorServices;


@SpringBootTest
@Testcontainers
public class ContractorIntegracionTest {
    
    @Autowired
    ContractorServices contractorServices;

    @Autowired
    JdbcTemplate jdbcTemplate;

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
    void init(){
        jdbcTemplate.update("DELETE FROM contractor");
    }

    @Test
    void saveAndGetContractorByIdTest(){
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
    void deleteContractorById(){
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

        contractorServices.deleteContractorById(testContractor.getId());

        Boolean result = jdbcTemplate.queryForObject(
            "SELECT is_active FROM contractor WHERE id = 'testId'",
            Boolean.class);

        assertEquals(false, result);
    }

    @Test
    void testAllPagination(){
    for(int i=1;i<=7;i++){
        jdbcTemplate.update("""
            INSERT INTO contractor
            (id, parent_id, name, name_full, inn, ogrn,country,
            industry,org_form) VALUES ( ?, null, 'ilya',
            'ilya bobkov', '12345', '123', 'ABH', 1, 1)
            """,
            "testId" + i);
        }
    var page0 = contractorServices.getAllContractorPagination(0, 3);
    assertEquals(3, page0.size());
    assertEquals("testId1", page0.get(0).getId());

    var page1 = contractorServices.getAllContractorPagination(1, 3);
    assertEquals(3, page1.size());
    assertEquals("testId4", page1.get(0).getId());
    }

    @Test
    void testFilter(){
        jdbcTemplate.update("""
            INSERT INTO contractor
            (id, parent_id, name, name_full, inn, ogrn,country,
            industry,org_form) VALUES ('testId1', null, 'ilya',
            'ilya bobkov', '12345', '123', 'ABH', 1, 1)
            """
        );

        jdbcTemplate.update("""
            INSERT INTO contractor
            (id, parent_id, name, name_full, inn, ogrn,country,
            industry,org_form) VALUES ('testId2', null, 'ilya',
            'ivan bobrov', '12345', '123', 'ABH', 1, 1)
            """
        );

        var filter = new SearchContractorRequestDto();
        var filter2 = new SearchContractorRequestDto();
        filter.setNameFull("bob");
        filter2.setNameFull("bobkov");;

        var page0 = contractorServices.searchContractors(filter,10, 0);
        assertEquals(2, page0.size());

        var page1 = contractorServices.searchContractors(filter2, 10, 0);
        assertEquals(1, page1.size());
        assertEquals("testId1", page1.get(0).getId());
    }
}
