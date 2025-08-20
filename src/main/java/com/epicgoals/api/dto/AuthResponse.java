// ABOUT_ME: Data transfer object for authentication responses
// ABOUT_ME: Contains user information and JWT tokens after successful authentication
package com.epicgoals.api.dto;

import java.util.UUID;

public record AuthResponse(
    UUID userId,
    String email,
    String accessToken,
    String refreshToken,
    long expiresIn // seconds
) {}