// ABOUT_ME: This file defines the request DTO for updating existing habits
// ABOUT_ME: Contains validation rules for habit updates including frequency and active state
package com.epicgoals.api.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public class UpdateHabitRequest {
    
    @NotBlank(message = "Habit name is required")
    @Size(max = 100, message = "Habit name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    
    @NotNull(message = "Frequency is required")
    @Min(value = 1, message = "Frequency must be at least 1")
    @Max(value = 21, message = "Frequency must not exceed 21")
    private Integer frequency;
    
    private Boolean isActive = true;
    
    // Default constructor
    public UpdateHabitRequest() {}
    
    // Constructor
    public UpdateHabitRequest(String name, UUID categoryId, Integer frequency, Boolean isActive) {
        this.name = name;
        this.categoryId = categoryId;
        this.frequency = frequency;
        this.isActive = isActive;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}