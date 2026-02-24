package com.nazir.realtimechat.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.access-token-expiration-minutes}")
    private long accessMinutes;

    @Value("${security.jwt.refresh-token-expiration-days}")
    private long refreshDays;

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, Instant.now().plus(accessMinutes, ChronoUnit.MINUTES));
    }

    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, Instant.now().plus(refreshDays, ChronoUnit.DAYS));
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractSubject(String token) {
        return getAllClaims(token).getSubject();
    }

    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private String buildToken(String subject, Map<String, Object> claims, Instant expiresAt) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiresAt))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
