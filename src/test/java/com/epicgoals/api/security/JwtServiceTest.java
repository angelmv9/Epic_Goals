// ABOUT_ME: Unit tests for JWT service token operations
// ABOUT_ME: Tests token generation, validation, parsing, and claims extraction
package com.epicgoals.api.security;

import com.epicgoals.api.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {
    
    private JwtService jwtService;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        String secret = "testSecretKey1234567890123456789012345678901234567890";
        jwtService = new JwtService(secret, 24, 30);
        
        testUser = new User("test@example.com", "hashedPassword");
        testUser.setId(UUID.randomUUID());
    }
    
    @Test
    void shouldGenerateValidAccessToken() {
        String token = jwtService.generateAccessToken(testUser);
        
        assertThat(token).isNotEmpty();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractTokenType(token)).isEqualTo("access");
    }
    
    @Test
    void shouldGenerateValidRefreshToken() {
        String token = jwtService.generateRefreshToken(testUser);
        
        assertThat(token).isNotEmpty();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractTokenType(token)).isEqualTo("refresh");
    }
    
    @Test
    void shouldExtractCorrectClaims() {
        String token = jwtService.generateAccessToken(testUser);
        
        UUID extractedUserId = jwtService.extractUserId(token);
        String extractedEmail = jwtService.extractEmail(token);
        
        assertThat(extractedUserId).isEqualTo(testUser.getId());
        assertThat(extractedEmail).isEqualTo(testUser.getEmail());
    }
    
    @Test
    void shouldParseTokenClaims() {
        String token = jwtService.generateAccessToken(testUser);
        
        Claims claims = jwtService.parseToken(token);
        
        assertThat(claims.getSubject()).isEqualTo(testUser.getId().toString());
        assertThat(claims.get("email")).isEqualTo(testUser.getEmail());
        assertThat(claims.get("type")).isEqualTo("access");
    }
    
    @Test
    void shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertThat(jwtService.isTokenValid(invalidToken)).isFalse();
    }
    
    @Test
    void shouldThrowExceptionWhenParsingInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertThatThrownBy(() -> jwtService.parseToken(invalidToken))
            .isInstanceOf(Exception.class);
    }
    
    @Test
    void shouldReturnCorrectExpirationTime() {
        long expirationSeconds = jwtService.getAccessTokenExpirationSeconds();
        
        assertThat(expirationSeconds).isEqualTo(24 * 3600);
    }
}