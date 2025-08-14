// ABOUT_ME: Basic integration tests for ProgressController to verify Spring context loads
// ABOUT_ME: Tests progress endpoints and service integration without full web layer
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.WeeklyScoreDto;
import com.epicgoals.api.entity.Category;
import com.epicgoals.api.entity.Habit;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.repository.CategoryRepository;
import com.epicgoals.api.repository.HabitRepository;
import com.epicgoals.api.repository.UserRepository;
import com.epicgoals.api.repository.WeeklyScoreRepository;
import com.epicgoals.api.service.ScoreService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProgressControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private WeeklyScoreRepository weeklyScoreRepository;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User("test@example.com", passwordEncoder.encode("password"));
        testUser = userRepository.save(testUser);

        // Create category
        testCategory = new Category(testUser, "Health", true);
        testCategory = categoryRepository.save(testCategory);
    }

    @AfterEach
    void tearDown() {
        weeklyScoreRepository.deleteAll();
        habitRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Verify Spring context loads properly
        assertNotNull(scoreService);
        assertNotNull(weeklyScoreRepository);
        assertNotNull(userRepository);
    }

    @Test
    void getCurrentWeekScore_WithNoHabits_ShouldReturnZeroScore() {
        // When
        WeeklyScoreDto result = scoreService.getCurrentWeekScore(testUser);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getScore());
        assertEquals(0, result.getCompletedHabits());
        assertEquals(0, result.getTotalHabits());
        assertEquals(LocalDate.now().with(DayOfWeek.MONDAY), result.getWeekStartDate());
    }

    @Test
    void getCurrentWeekScore_WithHabits_ShouldCalculateScore() {
        // Given - Create a habit
        Habit testHabit = new Habit(testUser, testCategory, "Exercise", 5);
        habitRepository.save(testHabit);

        // When
        WeeklyScoreDto result = scoreService.getCurrentWeekScore(testUser);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getScore()); // No completions yet, so 0%
        assertEquals(0, result.getCompletedHabits());
        assertEquals(5, result.getTotalHabits()); // Expected 5 times per week
        assertEquals(LocalDate.now().with(DayOfWeek.MONDAY), result.getWeekStartDate());
    }

    @Test
    void getHistoricalScores_ShouldReturnEmptyForNewUser() {
        // When
        List<WeeklyScoreDto> result = scoreService.getHistoricalScores(testUser, 12);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty()); // New user should have no historical scores
    }

    @Test
    void scoreCalculation_ShouldPersistToDatabase() {
        // Given - Create a habit
        Habit testHabit = new Habit(testUser, testCategory, "Exercise", 7);
        habitRepository.save(testHabit);

        // When - Calculate current week score
        WeeklyScoreDto result = scoreService.getCurrentWeekScore(testUser);

        // Then - Verify score was persisted
        assertNotNull(result.getId()); // Should have been saved to database
        
        // Verify we can retrieve it directly from repository
        boolean scoreExists = weeklyScoreRepository.findByUserAndWeekStartDate(
            testUser, LocalDate.now().with(DayOfWeek.MONDAY)
        ).isPresent();
        assertTrue(scoreExists);
    }
}