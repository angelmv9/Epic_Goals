// ABOUT_ME: Request DTO for updating existing goals with optional fields for partial updates
// ABOUT_ME: Primarily used for updating progress values and basic goal information
package com.epicgoals.api.dto;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record GoalUpdateRequest(
    @Size(max = 100)
    String name,
    
    @Size(max = 500)
    String description,
    
    String targetValue,
    
    String currentValue,
    
    UUID categoryId,
    
    Boolean isActive
) {}