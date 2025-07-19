package com.contractor.services;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.contractor.dto.UserRolesAddDto;

/**
 * service for connect with auth service
 */
@Service
public class ConnectAuthService {

    private RestTemplate restTemplate;

    public ConnectAuthService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
            .connectTimeout(Duration.ofSeconds(6))
            .readTimeout(Duration.ofSeconds(5))
            .build();
    }

    /**
     * createl header to connect auth service
     * @param adminToken token
     * @param userRolesAddDto dto with login and set of roles
     * @return header
     */
    public String connectAuthService(String adminToken, UserRolesAddDto userRolesAddDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(adminToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserRolesAddDto> request = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(
            "http://localhost8082/auth/user-roles/save",
            HttpMethod.POST,
            request,
            String.class
        ).getBody();
    }

}
