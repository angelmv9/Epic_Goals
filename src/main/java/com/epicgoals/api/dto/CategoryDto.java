// ABOUT_ME: This file defines the data transfer object for Category API responses
// ABOUT_ME: Contains category information without exposing internal entity structure
package com.epicgoals.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public class CategoryDto {
    
    private UUID id;
    
    @NotBlank
    @Size(max = 50)
    private String name;
    
    private Boolean isDefault;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    // Default constructor
    public CategoryDto() {}
    
    // Constructor with all fields
    public CategoryDto(UUID id, String name, Boolean isDefault, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
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
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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