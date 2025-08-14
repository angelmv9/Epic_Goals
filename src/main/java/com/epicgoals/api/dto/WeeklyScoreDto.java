// ABOUT_ME: This file defines the data transfer object for WeeklyScore API responses
// ABOUT_ME: Contains weekly score information without exposing internal entity relationships
package com.epicgoals.api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class WeeklyScoreDto {
    
    private UUID id;
    
    private LocalDate weekStartDate;
    
    private Integer score;
    
    private Integer completedHabits;
    
    private Integer totalHabits;
    
    private Instant calculatedAt;
    
    // Default constructor
    public WeeklyScoreDto() {}
    
    // Constructor with all fields
    public WeeklyScoreDto(UUID id, LocalDate weekStartDate, Integer score, 
                         Integer completedHabits, Integer totalHabits, Instant calculatedAt) {
        this.id = id;
        this.weekStartDate = weekStartDate;
        this.score = score;
        this.completedHabits = completedHabits;
        this.totalHabits = totalHabits;
        this.calculatedAt = calculatedAt;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }
    
    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public Integer getCompletedHabits() {
        return completedHabits;
    }
    
    public void setCompletedHabits(Integer completedHabits) {
        this.completedHabits = completedHabits;
    }
    
    public Integer getTotalHabits() {
        return totalHabits;
    }
    
    public void setTotalHabits(Integer totalHabits) {
        this.totalHabits = totalHabits;
    }
    
    public Instant getCalculatedAt() {
        return calculatedAt;
    }
    
    public void setCalculatedAt(Instant calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}