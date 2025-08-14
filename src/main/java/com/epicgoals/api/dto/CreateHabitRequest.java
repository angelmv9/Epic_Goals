// ABOUT_ME: This file defines the request DTO for creating new habits
// ABOUT_ME: Contains validation rules for habit creation including frequency constraints
package com.epicgoals.api.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public class CreateHabitRequest {
    
    @NotBlank(message = "Habit name is required")
    @Size(max = 100, message = "Habit name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    
    @NotNull(message = "Frequency is required")
    @Min(value = 1, message = "Frequency must be at least 1")
    @Max(value = 21, message = "Frequency must not exceed 21")
    private Integer frequency;
    
    // Default constructor
    public CreateHabitRequest() {}
    
    // Constructor
    public CreateHabitRequest(String name, UUID categoryId, Integer frequency) {
        this.name = name;
        this.categoryId = categoryId;
        this.frequency = frequency;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public UUID getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
    
    public Integer getFrequency() {
        return frequency;
    }
    
    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}