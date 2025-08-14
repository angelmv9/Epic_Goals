// ABOUT_ME: Service for JWT token generation, validation and parsing operations
// ABOUT_ME: Handles access tokens (24h) and refresh tokens (30d) with user claims
package com.epicgoals.api.security;

import com.epicgoals.api.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    
    private final SecretKey secretKey;
    private final long accessTokenExpirationHours;
    private final long refreshTokenExpirationDays;
    
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token.expiration-hours:24}") long accessTokenExpirationHours,
            @Value("${app.jwt.refresh-token.expiration-days:30}") long refreshTokenExpirationDays) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationHours = accessTokenExpirationHours;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }
    
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpirationHours, ChronoUnit.HOURS);
        
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
    
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenExpirationDays, ChronoUnit.DAYS);
        
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }
    
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public UUID extractUserId(String token) {
        Claims claims = parseToken(token);
        return UUID.fromString(claims.getSubject());
    }
    
    public String extractEmail(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }
    
    public String extractTokenType(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", String.class);
    }
    
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationHours * 3600;
    }
}