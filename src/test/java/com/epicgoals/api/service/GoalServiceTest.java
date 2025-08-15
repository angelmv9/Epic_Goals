// ABOUT_ME: Unit tests for GoalService business logic and validation rules
// ABOUT_ME: Tests goal CRUD operations, JSON validation, progress calculations, and hierarchy relationships
package com.epicgoals.api.service;

import com.epicgoals.api.dto.GoalCreateRequest;
import com.epicgoals.api.dto.GoalResponse;
import com.epicgoals.api.dto.GoalUpdateRequest;
import com.epicgoals.api.entity.*;
import com.epicgoals.api.repository.GoalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GoalService goalService;

    private User testUser;
    private Category testCategory;
    private Goal testGoal;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "hashedPassword");
        testUser.setId(UUID.randomUUID());

        testCategory = new Category(testUser, "Career", false);
        testCategory.setId(UUID.randomUUID());

        testGoal = new Goal(testUser, testCategory, GoalTimeframe.TWELVE_WEEK, GoalType.QUANTIFIABLE,
                          "Save Money", "Save for vacation", "{\"value\": 5000, \"unit\": \"dollars\"}");
        testGoal.setId(UUID.randomUUID());
        testGoal.setCreatedAt(Instant.now());
        testGoal.setUpdatedAt(Instant.now());

        goalService = new GoalService(goalRepository, categoryService, new ObjectMapper());
    }

    @Test
    void getUserGoals_ShouldReturnUserGoals() {
        // Arrange
        List<Goal> goals = Arrays.asList(testGoal);
        when(goalRepository.findByUserAndIsActiveTrue(testUser)).thenReturn(goals);

        // Act
        List<GoalResponse> result = goalService.getUserGoals(testUser);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testGoal.getName(), result.get(0).name());
        verify(goalRepository).findByUserAndIsActiveTrue(testUser);
    }

    @Test
    void getUserGoalsByTimeframe_ShouldReturnFilteredGoals() {
        // Arrange
        List<Goal> goals = Arrays.asList(testGoal);
        when(goalRepository.findByUserAndTimeframeAndIsActiveTrue(testUser, GoalTimeframe.TWELVE_WEEK))
                .thenReturn(goals);

        // Act
        List<GoalResponse> result = goalService.getUserGoalsByTimeframe(testUser, GoalTimeframe.TWELVE_WEEK);

        // Assert
        assertEquals(1, result.size());
        assertEquals(GoalTimeframe.TWELVE_WEEK, result.get(0).timeframe());
        verify(goalRepository).findByUserAndTimeframeAndIsActiveTrue(testUser, GoalTimeframe.TWELVE_WEEK);
    }

    @Test
    void getChildGoals_ShouldReturnChildGoals() {
        // Arrange
        UUID parentGoalId = UUID.randomUUID();
        List<Goal> childGoals = Arrays.asList(testGoal);
        when(goalRepository.findByUserAndParentGoalIdAndIsActiveTrue(testUser, parentGoalId))
                .thenReturn(childGoals);

        // Act
        List<GoalResponse> result = goalService.getChildGoals(testUser, parentGoalId);

        // Assert
        assertEquals(1, result.size());
        verify(goalRepository).findByUserAndParentGoalIdAndIsActiveTrue(testUser, parentGoalId);
    }

    @Test
    void createGoal_WithValidData_ShouldCreateGoal() {
        // Arrange
        GoalCreateRequest request = new GoalCreateRequest(
                GoalTimeframe.TWELVE_WEEK,
                GoalType.QUANTIFIABLE,
                "Save Money",
                "Save for vacation",
                "{\"value\": 5000, \"unit\": \"dollars\"}",
                "{\"value\": 1000, \"unit\": \"dollars\"}",
                testCategory.getId(),
                null
        );

        when(categoryService.getCategoryByIdAndUser(testCategory.getId(), testUser)).thenReturn(testCategory);
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // Act
        GoalResponse result = goalService.createGoal(testUser, request);

        // Assert
        assertNotNull(result);
        assertEquals(request.name(), result.name());
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void createGoal_WithParentGoal_ShouldValidateParentExists() {
        // Arrange
        UUID parentGoalId = UUID.randomUUID();
        GoalCreateRequest request = new GoalCreateRequest(
                GoalTimeframe.FOUR_WEEK,
                GoalType.QUANTIFIABLE,
                "Weekly Savings",
                "Weekly savings target",
                "{\"value\": 200, \"unit\": \"dollars\"}",
                null,
                null,
                parentGoalId
        );

        when(goalRepository.findByUserAndIdAndIsActiveTrue(testUser, parentGoalId))
                .thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // Act
        GoalResponse result = goalService.createGoal(testUser, request);

        // Assert
        assertNotNull(result);
        verify(goalRepository).findByUserAndIdAndIsActiveTrue(testUser, parentGoalId);
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void createGoal_WithInvalidParentGoal_ShouldThrowException() {
        // Arrange
        UUID parentGoalId = UUID.randomUUID();
        GoalCreateRequest request = new GoalCreateRequest(
                GoalTimeframe.FOUR_WEEK,
                GoalType.QUANTIFIABLE,
                "Weekly Savings",
                "Weekly savings target",
                "{\"value\": 200, \"unit\": \"dollars\"}",
                null,
                null,
                parentGoalId
        );

        when(goalRepository.findByUserAndIdAndIsActiveTrue(testUser, parentGoalId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            goalService.createGoal(testUser, request);
        });

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void updateGoal_WithValidData_ShouldUpdateGoal() {
        // Arrange
        GoalUpdateRequest request = new GoalUpdateRequest(
                "Updated Goal Name",
                "Updated description",
                "{\"value\": 6000, \"unit\": \"dollars\"}",
                "{\"value\": 2000, \"unit\": \"dollars\"}",
                null,
                null
        );

        when(goalRepository.findByUserAndIdAndIsActiveTrue(testUser, testGoal.getId()))
                .thenReturn(Optional.of(testGoal));
        when(goalRepository.save(testGoal)).thenReturn(testGoal);

        // Act
        GoalResponse result = goalService.updateGoal(testUser, testGoal.getId(), request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Goal Name", testGoal.getName());
        assertEquals("Updated description", testGoal.getDescription());
        verify(goalRepository).save(testGoal);
    }

    @Test
    void updateGoal_NonExistentGoal_ShouldThrowException() {
        // Arrange
        UUID goalId = UUID.randomUUID();
        GoalUpdateRequest request = new GoalUpdateRequest(
                "Updated Goal Name",
                null,
                null,
                null,
                null,
                null
        );

        when(goalRepository.findByUserAndIdAndIsActiveTrue(testUser, goalId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            goalService.updateGoal(testUser, goalId, request);
        });

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void deleteGoal_ExistingGoal_ShouldMarkAsInactive() {
        // Arrange
        when(goalRepository.findByUserAndIdAndIsActiveTrue(testUser, testGoal.getId()))
                .thenReturn(Optional.of(testGoal));
        when(goalRepository.save(testGoal)).thenReturn(testGoal);

        // Act
        goalService.deleteGoal(testUser, testGoal.getId());

        // Assert
        assertFalse(testGoal.getIsActive());
        verify(goalRepository).save(testGoal);
    }

    @Test
    void deleteGoal_NonExistentGoal_ShouldThrowException() {
        // Arrange
        UUID goalId = UUID.randomUUID();
        when(goalRepository.findByUserAndIdAndIsActiveTrue(testUser, goalId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            goalService.deleteGoal(testUser, goalId);
        });

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void createGoal_WithInvalidQuantifiableJson_ShouldThrowException() {
        // Arrange
        GoalCreateRequest request = new GoalCreateRequest(
                GoalTimeframe.TWELVE_WEEK,
                GoalType.QUANTIFIABLE,
                "Invalid Goal",
                "Invalid target value",
                "{\"invalid\": \"data\"}",
                null,
                null,
                null
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(testUser, request);
        });

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void createGoal_WithInvalidLevelBasedJson_ShouldThrowException() {
        // Arrange
        GoalCreateRequest request = new GoalCreateRequest(
                GoalTimeframe.TWELVE_WEEK,
                GoalType.LEVEL_BASED,
                "Invalid Level Goal",
                "Invalid level data",
                "{\"invalid\": \"data\"}",
                null,
                null,
                null
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(testUser, request);
        });

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void createGoal_WithInvalidQualitativeJson_ShouldThrowException() {
        // Arrange
        GoalCreateRequest request = new GoalCreateRequest(
                GoalTimeframe.TWELVE_WEEK,
                GoalType.QUALITATIVE,
                "Invalid Qualitative Goal",
                "Invalid qualitative data",
                "{\"invalid\": \"data\"}",
                null,
                null,
                null
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(testUser, request);
        });

        verify(goalRepository, never()).save(any(Goal.class));
    }
}