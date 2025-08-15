// ABOUT_ME: Request DTO for creating new goals with validation and type-specific fields
// ABOUT_ME: Validates goal data structures based on goal type before entity creation
package com.epicgoals.api.dto;

import com.epicgoals.api.entity.GoalTimeframe;
import com.epicgoals.api.entity.GoalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record GoalCreateRequest(
    @NotNull
    GoalTimeframe timeframe,
    
    @NotNull
    GoalType type,
    
    @NotBlank
    @Size(max = 100)
    String name,
    
    @Size(max = 500)
    String description,
    
    @NotBlank
    String targetValue,
    
    String currentValue,
    
    UUID categoryId,
    
    UUID parentGoalId
) {}