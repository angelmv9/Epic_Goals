// ABOUT_ME: Data transfer object for user login requests
// ABOUT_ME: Contains email and password fields for authentication
package com.epicgoals.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email,
    
    @NotBlank(message = "Password is required")
    String password
) {}