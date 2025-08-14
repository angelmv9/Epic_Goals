// ABOUT_ME: Data transfer object for user registration requests
// ABOUT_ME: Contains validation rules for email and password requirements
package com.epicgoals.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    String password,
    
    @NotBlank(message = "Password confirmation is required")
    String confirmPassword
) {}