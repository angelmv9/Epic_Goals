// ABOUT_ME: This file defines the WeeklyScore entity for tracking weekly habit completion rates
// ABOUT_ME: Stores calculated weekly scores as snapshots to preserve historical data over time
package com.epicgoals.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "weekly_scores",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "week_start_date"}),
       indexes = {
           @Index(name = "idx_weekly_scores_user", columnList = "user_id"),
           @Index(name = "idx_weekly_scores_week_start", columnList = "week_start_date"),
           @Index(name = "idx_weekly_scores_user_week", columnList = "user_id, week_start_date")
       })
public class WeeklyScore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;
    
    @Column(nullable = false)
    private Integer score;
    
    @Column(name = "completed_habits", nullable = false)
    private Integer completedHabits;
    
    @Column(name = "total_habits", nullable = false)
    private Integer totalHabits;
    
    @CreationTimestamp
    @Column(name = "calculated_at", nullable = false, updatable = false)
    private Instant calculatedAt;
    
    // Default constructor
    public WeeklyScore() {}
    
    // Constructor for creating weekly scores
    public WeeklyScore(User user, LocalDate weekStartDate, Integer score, 
                      Integer completedHabits, Integer totalHabits) {
        this.user = user;
        this.weekStartDate = weekStartDate;
        this.score = score;
        this.completedHabits = completedHabits;
        this.totalHabits = totalHabits;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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