// ABOUT_ME: Service providing business logic for goal management operations
// ABOUT_ME: Handles goal CRUD operations, progress calculations, and JSON data validation
package com.epicgoals.api.service;

import com.epicgoals.api.dto.GoalCreateRequest;
import com.epicgoals.api.dto.GoalResponse;
import com.epicgoals.api.dto.GoalUpdateRequest;
import com.epicgoals.api.entity.*;
import com.epicgoals.api.repository.GoalRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoalService {
    
    private final GoalRepository goalRepository;
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public GoalService(GoalRepository goalRepository, CategoryService categoryService, ObjectMapper objectMapper) {
        this.goalRepository = goalRepository;
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }
    
    @Transactional(readOnly = true)
    public List<GoalResponse> getUserGoals(User user) {
        List<Goal> goals = goalRepository.findByUserAndIsActiveTrue(user);
        return goals.stream()
                   .map(this::convertToResponse)
                   .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<GoalResponse> getUserGoalsByTimeframe(User user, GoalTimeframe timeframe) {
        List<Goal> goals = goalRepository.findByUserAndTimeframeAndIsActiveTrue(user, timeframe);
        return goals.stream()
                   .map(this::convertToResponse)
                   .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<GoalResponse> getChildGoals(User user, UUID parentGoalId) {
        List<Goal> goals = goalRepository.findByUserAndParentGoalIdAndIsActiveTrue(user, parentGoalId);
        return goals.stream()
                   .map(this::convertToResponse)
                   .collect(Collectors.toList());
    }
    
    public GoalResponse createGoal(User user, GoalCreateRequest request) {
        validateGoalTypeData(request.type(), request.targetValue());
        
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryService.getCategoryByIdAndUser(request.categoryId(), user);
        }
        
        // Validate parent goal exists and belongs to user
        if (request.parentGoalId() != null) {
            goalRepository.findByUserAndIdAndIsActiveTrue(user, request.parentGoalId())
                         .orElseThrow(() -> new EntityNotFoundException("Parent goal not found"));
        }
        
        Goal goal = new Goal(user, category, request.timeframe(), request.type(),
                           request.name(), request.description(), request.targetValue());
        goal.setCurrentValue(request.currentValue());
        goal.setParentGoalId(request.parentGoalId());
        
        Goal savedGoal = goalRepository.save(goal);
        return convertToResponse(savedGoal);
    }
    
    public GoalResponse updateGoal(User user, UUID goalId, GoalUpdateRequest request) {
        Goal goal = goalRepository.findByUserAndIdAndIsActiveTrue(user, goalId)
                                 .orElseThrow(() -> new EntityNotFoundException("Goal not found"));
        
        if (request.name() != null) {
            goal.setName(request.name());
        }
        
        if (request.description() != null) {
            goal.setDescription(request.description());
        }
        
        if (request.targetValue() != null) {
            validateGoalTypeData(goal.getType(), request.targetValue());
            goal.setTargetValue(request.targetValue());
        }
        
        if (request.currentValue() != null) {
            validateGoalTypeData(goal.getType(), request.currentValue());
            goal.setCurrentValue(request.currentValue());
        }
        
        if (request.categoryId() != null) {
            Category category = categoryService.getCategoryByIdAndUser(request.categoryId(), user);
            goal.setCategory(category);
        }
        
        if (request.isActive() != null) {
            goal.setIsActive(request.isActive());
        }
        
        Goal savedGoal = goalRepository.save(goal);
        return convertToResponse(savedGoal);
    }
    
    public void deleteGoal(User user, UUID goalId) {
        Goal goal = goalRepository.findByUserAndIdAndIsActiveTrue(user, goalId)
                                 .orElseThrow(() -> new EntityNotFoundException("Goal not found"));
        
        goal.setIsActive(false);
        goalRepository.save(goal);
    }
    
    private void validateGoalTypeData(GoalType type, String jsonData) {
        if (jsonData == null || jsonData.trim().isEmpty()) {
            return;
        }
        
        try {
            JsonNode node = objectMapper.readTree(jsonData);
            
            switch (type) {
                case QUANTIFIABLE:
                    if (!node.has("value") || !node.has("unit")) {
                        throw new IllegalArgumentException("Quantifiable goals must have 'value' and 'unit' fields");
                    }
                    if (!node.get("value").isNumber()) {
                        throw new IllegalArgumentException("Quantifiable goal value must be a number");
                    }
                    break;
                    
                case LEVEL_BASED:
                    if (!node.has("level")) {
                        throw new IllegalArgumentException("Level-based goals must have 'level' field");
                    }
                    break;
                    
                case QUALITATIVE:
                    if (!node.has("rating") || !node.has("scale")) {
                        throw new IllegalArgumentException("Qualitative goals must have 'rating' and 'scale' fields");
                    }
                    if (!node.get("rating").isNumber() || !node.get("scale").isNumber()) {
                        throw new IllegalArgumentException("Qualitative goal rating and scale must be numbers");
                    }
                    break;
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format for goal data", e);
        }
    }
    
    private Double calculateProgressPercentage(Goal goal) {
        if (goal.getCurrentValue() == null || goal.getTargetValue() == null) {
            return 0.0;
        }
        
        try {
            JsonNode currentNode = objectMapper.readTree(goal.getCurrentValue());
            JsonNode targetNode = objectMapper.readTree(goal.getTargetValue());
            
            switch (goal.getType()) {
                case QUANTIFIABLE:
                    double currentValue = currentNode.get("value").asDouble();
                    double targetValue = targetNode.get("value").asDouble();
                    if (targetValue == 0) return 0.0;
                    return Math.min(100.0, (currentValue / targetValue) * 100.0);
                    
                case LEVEL_BASED:
                    if (targetNode.has("allLevels")) {
                        JsonNode levelsArray = targetNode.get("allLevels");
                        String currentLevel = currentNode.get("level").asText();
                        String targetLevel = targetNode.get("level").asText();
                        
                        int currentIndex = -1;
                        int targetIndex = -1;
                        
                        for (int i = 0; i < levelsArray.size(); i++) {
                            String level = levelsArray.get(i).asText();
                            if (level.equals(currentLevel)) currentIndex = i;
                            if (level.equals(targetLevel)) targetIndex = i;
                        }
                        
                        if (currentIndex >= 0 && targetIndex >= 0 && targetIndex > 0) {
                            return Math.min(100.0, (double) currentIndex / targetIndex * 100.0);
                        }
                    }
                    return 0.0;
                    
                case QUALITATIVE:
                    double currentRating = currentNode.get("rating").asDouble();
                    double targetRating = targetNode.get("rating").asDouble();
                    double scale = targetNode.get("scale").asDouble();
                    if (scale == 0) return 0.0;
                    return Math.min(100.0, (currentRating / targetRating) * 100.0);
                    
                default:
                    return 0.0;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private GoalResponse convertToResponse(Goal goal) {
        String categoryName = goal.getCategory() != null ? goal.getCategory().getName() : null;
        UUID categoryId = goal.getCategory() != null ? goal.getCategory().getId() : null;
        Double progressPercentage = calculateProgressPercentage(goal);
        
        return new GoalResponse(
            goal.getId(),
            goal.getTimeframe(),
            goal.getType(),
            goal.getName(),
            goal.getDescription(),
            goal.getTargetValue(),
            goal.getCurrentValue(),
            categoryId,
            categoryName,
            goal.getParentGoalId(),
            goal.getIsActive(),
            progressPercentage,
            goal.getCreatedAt(),
            goal.getUpdatedAt()
        );
    }
}