// ABOUT_ME: Service for user authentication operations including registration and login
// ABOUT_ME: Handles password validation, BCrypt hashing, and JWT token generation
package com.epicgoals.api.service;

import com.epicgoals.api.dto.AuthResponse;
import com.epicgoals.api.dto.LoginRequest;
import com.epicgoals.api.dto.RegisterRequest;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.repository.UserRepository;
import com.epicgoals.api.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    public AuthResponse register(RegisterRequest request) {
        // Check if passwords match
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Create and save user
        User user = new User(request.email(), passwordEncoder.encode(request.password()));
        user = userRepository.save(user);
        
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(
            user.getId(),
            user.getEmail(),
            accessToken,
            refreshToken,
            jwtService.getAccessTokenExpirationSeconds()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        
        // Check password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(
            user.getId(),
            user.getEmail(),
            accessToken,
            refreshToken,
            jwtService.getAccessTokenExpirationSeconds()
        );
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
        
        // Check token type
        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new BadCredentialsException("Invalid token type");
        }
        
        // Extract user and generate new tokens
        UUID userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        
        return new AuthResponse(
            user.getId(),
            user.getEmail(),
            newAccessToken,
            newRefreshToken,
            jwtService.getAccessTokenExpirationSeconds()
        );
    }
}