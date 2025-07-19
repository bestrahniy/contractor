package com.contractor.config;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecutityCongig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter rolesConvert = new JwtGrantedAuthoritiesConverter();
        rolesConvert.setAuthorityPrefix("ROLE_");
        rolesConvert.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter authConvert = new JwtAuthenticationConverter();
        authConvert.setJwtGrantedAuthoritiesConverter(rolesConvert);
        return authConvert;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity httpSecurity,
        JwtAuthenticationConverter jwtConverter,
        JwtDecoder jwtDecoder) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(request -> request
            .requestMatchers(HttpMethod.GET, "/ui/contractor/deals", "/ui/contractor/{id}",
                    "/all/{page}/{size}", "/ui/contractor/search")
                .hasAnyRole("USER", "CONTRACTOR_SEPERUSER",
                "CONTRACTOR_RUS", "SUPERUSER")
            .requestMatchers(HttpMethod.PUT, "/ui/contractor/save")
                .hasAnyRole("CONTRACTOR_SUPERUSER", "SUPERUSER")
            .requestMatchers(HttpMethod.DELETE, "/ui/contractor/delete/{id}")
                .hasAnyRole("SUPERUSER", "CONTRACTOR_SUPERUSER")
            .requestMatchers(HttpMethod.PUT, "/ui/contractor/user-roles/save")
                .hasAnyRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/ui/contractor/user-roles/{login}")
                .hasAnyRole("USER", "CONTRACTOR_RUS", "CONTRACTOR_SUPERUSER",
                    "SUPERUSER", "ADMIN", "CREDIT_USER", "OVERDRAFT_USER", "DEAL_SUPERUSER")
            .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder)
                    .jwtAuthenticationConverter(jwtConverter)
                )
            );
        return httpSecurity.build();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "security.jwt.secret_key")
    public JwtDecoder hmacJwtDecoder(
        @Value("${security.jwt.secret_key}") String secretKeyHex
    ) throws DecoderException {
        byte[] keyBytes = org.apache.commons.codec.binary.Hex.decodeHex(secretKeyHex.toCharArray());
        javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    @ConditionalOnProperty(name = "security.oauth2.resourceserver.jwt.jwk-set-uri")
    public JwtDecoder jwkSetUriProperty(@Value("${security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkUri) {
        return NimbusJwtDecoder.withJwkSetUri(jwkUri).build();
    }

}
