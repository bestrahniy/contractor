package com.contractor.filter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{

    @Value("${security.jwt.secret_key}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            String jwt = null;
            String userName = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    jwt = authHeader.substring(7);
                    userName = parseJwt(jwt).getPayload().getSubject();
            }

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                @SuppressWarnings("unchecked")
                List<String> roles = parseJwt(jwt).getPayload().get("roles", List.class);
                List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

                if (!parseJwt(jwt).getPayload().getExpiration().before(new Date())) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userName, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } catch (ExpiredJwtException JwtException) {
            request.setAttribute("exception", JwtException);
        } catch (JwtException | IllegalArgumentException | DecoderException exception) {
            SecurityContextHolder.clearContext();
            request.setAttribute("exception", response);
        }
        filterChain.doFilter(request, response);
    }

    public Jws<Claims> parseJwt(String jwt) throws JwtException, IllegalArgumentException, DecoderException {
        return Jwts.parser().verifyWith(getSigninKey())
            .build().parseSignedClaims(jwt);
    }

    public SecretKey getSigninKey() throws DecoderException {
        byte[] keyBytes = org.apache.commons.codec.binary.Hex.decodeHex(secretKey.toCharArray());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}