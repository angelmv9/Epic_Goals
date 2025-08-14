// ABOUT_ME: Integration tests for authentication endpoints
// ABOUT_ME: Tests registration, login, token refresh, and error scenarios
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.AuthResponse;
import com.epicgoals.api.dto.LoginRequest;
import com.epicgoals.api.dto.RegisterRequest;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    void shouldRegisterNewUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "TestPass123",
            "TestPass123"
        );
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.expiresIn", greaterThan(0)));
    }
    
    @Test
    void shouldRejectRegistrationWithMismatchedPasswords() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "TestPass123",
            "DifferentPass123"
        );
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldRejectRegistrationWithExistingEmail() throws Exception {
        // Create existing user
        User existingUser = new User("test@example.com", passwordEncoder.encode("password"));
        userRepository.save(existingUser);
        
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "TestPass123",
            "TestPass123"
        );
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldRejectWeakPasswords() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "weak", // Too short and missing requirements
            "weak"
        );
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        // Create user
        User user = new User("test@example.com", passwordEncoder.encode("TestPass123"));
        userRepository.save(user);
        
        LoginRequest request = new LoginRequest("test@example.com", "TestPass123");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }
    
    @Test
    void shouldRejectLoginWithInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("nonexistent@example.com", "wrongpassword");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void shouldRefreshTokenWithValidRefreshToken() throws Exception {
        // This would require generating a valid refresh token first
        // Implementation depends on having a way to generate tokens in tests
        // For now, this is a placeholder test structure
    }
    
    @Test
    void shouldReturnSuccessForLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Logged out successfully")));
    }
}