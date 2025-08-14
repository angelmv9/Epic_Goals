// ABOUT_ME: Basic integration tests for CategoryController to verify Spring context loads
// ABOUT_ME: Tests repository operations and service integration without full web layer
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.CategoryDto;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.repository.CategoryRepository;
import com.epicgoals.api.repository.UserRepository;
import com.epicgoals.api.service.CategoryService;
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
class CategoryControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User("test@example.com", passwordEncoder.encode("password"));
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Verify Spring context loads properly
        assertNotNull(categoryService);
        assertNotNull(categoryRepository);
        assertNotNull(userRepository);
    }

    @Test
    void createDefaultCategories_ShouldWork() {
        // Test that default categories are created
        categoryService.createDefaultCategoriesForUser(testUser);
        
        List<CategoryDto> categories = categoryService.getUserCategories(testUser);
        
        assertEquals(5, categories.size());
        assertTrue(categories.stream().anyMatch(c -> "Health".equals(c.getName()) && c.getIsDefault()));
        assertTrue(categories.stream().anyMatch(c -> "Career".equals(c.getName()) && c.getIsDefault()));
        assertTrue(categories.stream().anyMatch(c -> "Family".equals(c.getName()) && c.getIsDefault()));
        assertTrue(categories.stream().anyMatch(c -> "Finances".equals(c.getName()) && c.getIsDefault()));
        assertTrue(categories.stream().anyMatch(c -> "Wisdom".equals(c.getName()) && c.getIsDefault()));
    }
}