// ABOUT_ME: Basic integration tests for HabitController to verify Spring context loads
// ABOUT_ME: Tests repository operations and service integration without full web layer
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.CreateHabitRequest;
import com.epicgoals.api.dto.HabitDto;
import com.epicgoals.api.entity.Category;
import com.epicgoals.api.entity.Habit;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.repository.CategoryRepository;
import com.epicgoals.api.repository.HabitCompletionRepository;
import com.epicgoals.api.repository.HabitRepository;
import com.epicgoals.api.repository.UserRepository;
import com.epicgoals.api.service.HabitService;
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
class HabitControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitCompletionRepository habitCompletionRepository;

    @Autowired
    private HabitService habitService;

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
        habitCompletionRepository.deleteAll();
        habitRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Verify Spring context loads properly
        assertNotNull(habitService);
        assertNotNull(habitRepository);
        assertNotNull(categoryRepository);
        assertNotNull(userRepository);
    }

    @Test
    void createHabit_ShouldWork() {
        // Test that habits can be created
        CreateHabitRequest request = new CreateHabitRequest("Exercise", testCategory.getId(), 5);
        HabitDto habit = habitService.createHabit(testUser, request);
        
        assertNotNull(habit);
        assertEquals("Exercise", habit.getName());
        assertEquals(5, habit.getFrequency());
        assertEquals(testCategory.getId(), habit.getCategoryId());
        assertTrue(habit.getIsActive());
    }

    @Test
    void habitLimit_ShouldBeEnforced() {
        // Create 15 habits to reach limit
        for (int i = 1; i <= 15; i++) {
            CreateHabitRequest request = new CreateHabitRequest("Habit " + i, testCategory.getId(), 5);
            habitService.createHabit(testUser, request);
        }
        
        // Try to create 16th habit
        CreateHabitRequest request = new CreateHabitRequest("16th Habit", testCategory.getId(), 5);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            habitService.createHabit(testUser, request)
        );
        assertTrue(exception.getMessage().contains("Maximum of 15 habits allowed"));
    }
}