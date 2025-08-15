// ABOUT_ME: Basic integration tests for GoalController to verify Spring context loads
// ABOUT_ME: Tests goal service operations and business logic without full web layer
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.GoalCreateRequest;
import com.epicgoals.api.dto.GoalResponse;
import com.epicgoals.api.entity.Category;
import com.epicgoals.api.entity.GoalTimeframe;
import com.epicgoals.api.entity.GoalType;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.repository.CategoryRepository;
import com.epicgoals.api.repository.GoalRepository;
import com.epicgoals.api.repository.UserRepository;
import com.epicgoals.api.service.GoalService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GoalControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalService goalService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", passwordEncoder.encode("password"));
        testUser = userRepository.save(testUser);

        testCategory = new Category(testUser, "Career", true);
        testCategory = categoryRepository.save(testCategory);
    }

    @AfterEach
    void tearDown() {
        goalRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Verify Spring context loads properly
        assertNotNull(goalService);
        assertNotNull(goalRepository);
        assertNotNull(categoryRepository);
        assertNotNull(userRepository);
    }

    @Test
    void createGoal_ShouldWork() {
        // Test that goals can be created
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

        GoalResponse goal = goalService.createGoal(testUser, request);

        assertNotNull(goal);
        assertEquals("Save Money", goal.name());
        assertEquals(GoalTimeframe.TWELVE_WEEK, goal.timeframe());
        assertEquals(GoalType.QUANTIFIABLE, goal.type());
        assertEquals(testCategory.getId(), goal.categoryId());
        assertTrue(goal.isActive());
    }

    @Test
    void getUserGoals_ShouldReturnUserGoals() {
        // Create a goal first
        GoalCreateRequest request = new GoalCreateRequest(
                GoalTimeframe.TWELVE_WEEK,
                GoalType.QUANTIFIABLE,
                "Save Money",
                "Save for vacation",
                "{\"value\": 5000, \"unit\": \"dollars\"}",
                null,
                testCategory.getId(),
                null
        );
        goalService.createGoal(testUser, request);

        // Test retrieval
        List<GoalResponse> goals = goalService.getUserGoals(testUser);

        assertEquals(1, goals.size());
        assertEquals("Save Money", goals.get(0).name());
    }

    @Test
    void getUserGoalsByTimeframe_ShouldFilterCorrectly() {
        // Create goals with different timeframes
        GoalCreateRequest twelveWeekGoal = new GoalCreateRequest(
                GoalTimeframe.TWELVE_WEEK,
                GoalType.QUANTIFIABLE,
                "12 Week Goal",
                "Short term goal",
                "{\"value\": 1000, \"unit\": \"dollars\"}",
                null,
                null,
                null
        );

        GoalCreateRequest fiveYearGoal = new GoalCreateRequest(
                GoalTimeframe.FIVE_YEAR,
                GoalType.QUANTIFIABLE,
                "5 Year Goal",
                "Long term goal",
                "{\"value\": 50000, \"unit\": \"dollars\"}",
                null,
                null,
                null
        );

        goalService.createGoal(testUser, twelveWeekGoal);
        goalService.createGoal(testUser, fiveYearGoal);

        // Test filtering
        List<GoalResponse> twelveWeekGoals = goalService.getUserGoalsByTimeframe(testUser, GoalTimeframe.TWELVE_WEEK);
        List<GoalResponse> fiveYearGoals = goalService.getUserGoalsByTimeframe(testUser, GoalTimeframe.FIVE_YEAR);

        assertEquals(1, twelveWeekGoals.size());
        assertEquals("12 Week Goal", twelveWeekGoals.get(0).name());

        assertEquals(1, fiveYearGoals.size());
        assertEquals("5 Year Goal", fiveYearGoals.get(0).name());
    }

    @Test
    void createGoal_WithInvalidJson_ShouldThrowException() {
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

        assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(testUser, request);
        });
    }
}