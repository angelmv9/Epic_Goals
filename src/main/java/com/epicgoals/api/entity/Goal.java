// ABOUT_ME: Goal entity representing multi-timeframe goals with different types and hierarchical structure
// ABOUT_ME: Supports quantifiable, level-based, and qualitative goals with JSON storage for flexible data structures
package com.epicgoals.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "goals")
public class Goal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalTimeframe timeframe;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType type;
    
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;
    
    @Size(max = 500)
    @Column(length = 500)
    private String description;
    
    @Column(name = "target_value", columnDefinition = "TEXT")
    private String targetValue;
    
    @Column(name = "current_value", columnDefinition = "TEXT")
    private String currentValue;
    
    @Column(name = "parent_goal_id")
    private UUID parentGoalId;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Default constructor
    public Goal() {}
    
    // Constructor for creating goals
    public Goal(User user, Category category, GoalTimeframe timeframe, GoalType type, 
                String name, String description, String targetValue) {
        this.user = user;
        this.category = category;
        this.timeframe = timeframe;
        this.type = type;
        this.name = name;
        this.description = description;
        this.targetValue = targetValue;
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
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public GoalTimeframe getTimeframe() {
        return timeframe;
    }
    
    public void setTimeframe(GoalTimeframe timeframe) {
        this.timeframe = timeframe;
    }
    
    public GoalType getType() {
        return type;
    }
    
    public void setType(GoalType type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getTargetValue() {
        return targetValue;
    }
    
    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }
    
    public String getCurrentValue() {
        return currentValue;
    }
    
    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }
    
    public UUID getParentGoalId() {
        return parentGoalId;
    }
    
    public void setParentGoalId(UUID parentGoalId) {
        this.parentGoalId = parentGoalId;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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