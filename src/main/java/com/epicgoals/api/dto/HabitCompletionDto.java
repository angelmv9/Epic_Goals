// ABOUT_ME: This file defines the data transfer object for HabitCompletion API responses  
// ABOUT_ME: Contains completion information for tracking habit progress over time
package com.epicgoals.api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class HabitCompletionDto {
    
    private UUID id;
    
    private UUID habitId;
    
    private LocalDate date;
    
    private Boolean completed;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    // Default constructor
    public HabitCompletionDto() {}
    
    // Constructor with all fields
    public HabitCompletionDto(UUID id, UUID habitId, LocalDate date, Boolean completed, 
                             Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.habitId = habitId;
        this.date = date;
        this.completed = completed;
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
    
    public UUID getHabitId() {
        return habitId;
    }
    
    public void setHabitId(UUID habitId) {
        this.habitId = habitId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Boolean getCompleted() {
        return completed;
    }
    
    public void setCompleted(Boolean completed) {
        this.completed = completed;
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