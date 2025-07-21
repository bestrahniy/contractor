package com.contractor.controller.ContractorTest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.contractor.dto.GetContactorByIdDto;
import com.contractor.dto.SaveContractorDto;
import com.contractor.services.ContractorServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ContractorUiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContractorServices contractorServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17"));

    private String token;

    private String adminToken;

    private Key key;

    private static String secretKey = "b55a3f4a6400c4ef85c16187653713004986bace196af0d78c24b0d1ca26cd9a";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.change-log", () ->  "classpath:db/changelog/db.changelog-master.yaml");
        registry.add("security.jwt.secret_key", () -> secretKey);
    }

    @BeforeEach
    void adminTokenInit() throws DecoderException {
        byte[] keyBytes = org.apache.commons.codec.binary.Hex.decodeHex(secretKey.toCharArray());
        key = Keys.hmacShaKeyFor(keyBytes);

        Instant now = Instant.now();
        adminToken = Jwts.builder()
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(3600)))
            .notBefore(Date.from(now))
            .signWith(key)
            .subject("adminUSer")
            .claim("roles", "ADMIN")
            .compact();

        token = createToken(Set.of("SUPERUSER"));
        for (int i = 0; i <= 10; i++) {
            SaveContractorDto dto = new SaveContractorDto();
            dto.setId("testCtr" + i);
            dto.setName("ilya" + i);
            dto.setNameFull("ilya bobkov");
            dto.setInn("12345");
            dto.setOgrn("123");
            dto.setOrgForm(1);
            dto.setIndustry(1);
            dto.setCountry("RUS");
            contractorServices.saveContractor(dto);
        }
        token = null;
    }

    private String createToken(Set<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
            .issuedAt(Date.from(now))
            .notBefore(Date.from(now))
            .expiration(Date.from(now.plusSeconds(3600)))
            .subject("testUser")
            .claim("roles", roles)
            .signWith(key)
            .compact();
    }

    @Test
    void whenTokenIsValid_thenAccess() throws Exception {
        token = createToken(Set.of("USER"));

        mockMvc.perform(get("/ui/contractor/deals")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void whenAdminToken_thenAccessToUiDealsIsDisable() throws Exception {
        mockMvc.perform(get("/ui/contractor/deals")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void whenTokenConstainsContractorRusRole_thenListContractorHasOnlyRus() throws Exception {
        token = createToken(Set.of("USER", "CONTRACTOR_RUS"));

        MvcResult mvcResult = mockMvc.perform(get("/ui/contractor/deals")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        String responce = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<GetContactorByIdDto> dtos = objectMapper.readValue(responce, new TypeReference<List<GetContactorByIdDto>>(){});
        assertFalse(dtos.isEmpty());
        boolean allCountryRus = dtos.stream()
            .allMatch(dto -> dto.getCountry().getId().equals("RUS"));
        assertTrue(allCountryRus);
    }

    @Test
    void whenCheckUserRoles_thenReturnSetRoles() throws JsonProcessingException, Exception {
        token = createToken(Set.of("USER", "SUPERUSER"));

        String login = "testUser";
        mockMvc.perform(get("/ui/contractor/user-roles/{login}", login)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.length()").value(2));
    }

}
