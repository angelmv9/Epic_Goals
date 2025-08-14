// ABOUT_ME: REST controller for user authentication endpoints
// ABOUT_ME: Handles registration, login, token refresh, and logout operations
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.AuthResponse;
import com.epicgoals.api.dto.LoginRequest;
import com.epicgoals.api.dto.RegisterRequest;
import com.epicgoals.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // Since we're using stateless JWT tokens, logout is handled client-side
        // by removing tokens from storage. This endpoint exists for consistency
        // and could be extended with token blacklisting in the future.
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}