// ABOUT_ME: Response DTO for Goal API operations including calculated progress information
// ABOUT_ME: Contains full goal data with category details and computed progress percentages
package com.epicgoals.api.dto;

import com.epicgoals.api.entity.GoalTimeframe;
import com.epicgoals.api.entity.GoalType;

import java.time.Instant;
import java.util.UUID;

public record GoalResponse(
    UUID id,
    GoalTimeframe timeframe,
    GoalType type,
    String name,
    String description,
    String targetValue,
    String currentValue,
    UUID categoryId,
    String categoryName,
    UUID parentGoalId,
    Boolean isActive,
    Double progressPercentage,
    Instant createdAt,
    Instant updatedAt
) {}