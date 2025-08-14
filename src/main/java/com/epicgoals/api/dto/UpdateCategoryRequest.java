// ABOUT_ME: This file defines the request DTO for updating existing categories
// ABOUT_ME: Contains validation rules for category updates with name constraints
package com.epicgoals.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCategoryRequest {
    
    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    private String name;
    
    // Default constructor
    public UpdateCategoryRequest() {}
    
    // Constructor
    public UpdateCategoryRequest(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}