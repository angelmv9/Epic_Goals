// ABOUT_ME: This file defines the data transfer object for Habit API responses
// ABOUT_ME: Contains habit information including category details without exposing entity relationships
package com.epicgoals.api.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.UUID;

public class HabitDto {
    
    private UUID id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Min(1)
    @Max(21)
    private Integer frequency;
    
    private Boolean isActive;
    
    private UUID categoryId;
    
    private String categoryName;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    // Default constructor
    public HabitDto() {}
    
    // Constructor with all fields
    public HabitDto(UUID id, String name, Integer frequency, Boolean isActive, 
                   UUID categoryId, String categoryName, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
        this.isActive = isActive;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public UUID getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}