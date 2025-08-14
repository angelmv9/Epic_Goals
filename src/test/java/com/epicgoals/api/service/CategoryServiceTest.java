// ABOUT_ME: Unit tests for CategoryService business logic and data validation
// ABOUT_ME: Tests category CRUD operations, default category creation, and security boundaries
package com.epicgoals.api.service;

import com.epicgoals.api.dto.CategoryDto;
import com.epicgoals.api.dto.CreateCategoryRequest;
import com.epicgoals.api.dto.UpdateCategoryRequest;
import com.epicgoals.api.entity.Category;
import com.epicgoals.api.entity.Habit;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.repository.CategoryRepository;
import com.epicgoals.api.repository.HabitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User testUser;
    private Category defaultCategory;
    private Category customCategory;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "hashedPassword");
        testUser.setId(UUID.randomUUID());
        
        defaultCategory = new Category(testUser, "Health", true);
        defaultCategory.setId(UUID.randomUUID());
        
        customCategory = new Category(testUser, "Custom", false);
        customCategory.setId(UUID.randomUUID());
    }

    @Test
    void createDefaultCategoriesForUser_ShouldCreateAllDefaultCategories() {
        // Given
        when(categoryRepository.existsByUserAndName(eq(testUser), anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        categoryService.createDefaultCategoriesForUser(testUser);

        // Then
        verify(categoryRepository, times(5)).save(argThat(category -> 
            category.getUser().equals(testUser) && 
            category.getIsDefault() == true &&
            Arrays.asList("Career", "Health", "Family", "Finances", "Wisdom").contains(category.getName())
        ));
    }

    @Test
    void createDefaultCategoriesForUser_ShouldSkipExistingCategories() {
        // Given
        when(categoryRepository.existsByUserAndName(testUser, "Health")).thenReturn(true);
        when(categoryRepository.existsByUserAndName(testUser, "Career")).thenReturn(false);
        when(categoryRepository.existsByUserAndName(testUser, "Family")).thenReturn(false);
        when(categoryRepository.existsByUserAndName(testUser, "Finances")).thenReturn(false);
        when(categoryRepository.existsByUserAndName(testUser, "Wisdom")).thenReturn(false);

        // When
        categoryService.createDefaultCategoriesForUser(testUser);

        // Then
        verify(categoryRepository, times(4)).save(any(Category.class));
        verify(categoryRepository, never()).save(argThat(category -> "Health".equals(category.getName())));
    }

    @Test
    void getUserCategories_ShouldReturnAllUserCategories() {
        // Given
        List<Category> categories = Arrays.asList(defaultCategory, customCategory);
        when(categoryRepository.findByUserOrderByName(testUser)).thenReturn(categories);

        // When
        List<CategoryDto> result = categoryService.getUserCategories(testUser);

        // Then
        assertEquals(2, result.size());
        assertEquals("Health", result.get(0).getName());
        assertEquals("Custom", result.get(1).getName());
    }

    @Test
    void createCategory_ShouldCreateValidCategory() {
        // Given
        CreateCategoryRequest request = new CreateCategoryRequest("NewCategory");
        when(categoryRepository.existsByUserAndName(testUser, "NewCategory")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryDto result = categoryService.createCategory(testUser, request);

        // Then
        assertEquals("NewCategory", result.getName());
        assertEquals(false, result.getIsDefault());
        verify(categoryRepository).save(argThat(category -> 
            category.getName().equals("NewCategory") && 
            category.getUser().equals(testUser) &&
            category.getIsDefault() == false
        ));
    }

    @Test
    void createCategory_ShouldThrowExceptionForDuplicateName() {
        // Given
        CreateCategoryRequest request = new CreateCategoryRequest("Health");
        when(categoryRepository.existsByUserAndName(testUser, "Health")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            categoryService.createCategory(testUser, request)
        );
        assertEquals("Category with name 'Health' already exists", exception.getMessage());
    }

    @Test
    void updateCategory_ShouldUpdateValidCategory() {
        // Given
        UpdateCategoryRequest request = new UpdateCategoryRequest("UpdatedCustom");
        when(categoryRepository.findByIdAndUser(customCategory.getId(), testUser))
            .thenReturn(Optional.of(customCategory));
        when(categoryRepository.existsByUserAndName(testUser, "UpdatedCustom")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CategoryDto result = categoryService.updateCategory(testUser, customCategory.getId(), request);

        // Then
        assertEquals("UpdatedCustom", result.getName());
        verify(categoryRepository).save(customCategory);
        assertEquals("UpdatedCustom", customCategory.getName());
    }

    @Test
    void updateCategory_ShouldThrowExceptionForDefaultCategory() {
        // Given
        UpdateCategoryRequest request = new UpdateCategoryRequest("NewName");
        when(categoryRepository.findByIdAndUser(defaultCategory.getId(), testUser))
            .thenReturn(Optional.of(defaultCategory));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            categoryService.updateCategory(testUser, defaultCategory.getId(), request)
        );
        assertEquals("Cannot modify default categories", exception.getMessage());
    }

    @Test
    void deleteCategory_ShouldDeleteAndReassignHabits() {
        // Given
        Category uncategorizedCategory = new Category(testUser, "Uncategorized", true);
        Habit habit1 = new Habit(testUser, customCategory, "Habit1", 5);
        Habit habit2 = new Habit(testUser, customCategory, "Habit2", 3);
        List<Habit> habits = Arrays.asList(habit1, habit2);

        when(categoryRepository.findByIdAndUser(customCategory.getId(), testUser))
            .thenReturn(Optional.of(customCategory));
        when(habitRepository.findByCategory(customCategory)).thenReturn(habits);
        when(categoryRepository.findByUserAndIsDefaultTrue(testUser))
            .thenReturn(Arrays.asList(uncategorizedCategory));
        when(habitRepository.save(any(Habit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        categoryService.deleteCategory(testUser, customCategory.getId());

        // Then
        verify(habitRepository, times(2)).save(any(Habit.class));
        verify(categoryRepository).delete(customCategory);
        assertEquals(uncategorizedCategory, habit1.getCategory());
        assertEquals(uncategorizedCategory, habit2.getCategory());
    }

    @Test
    void deleteCategory_ShouldThrowExceptionForDefaultCategory() {
        // Given
        when(categoryRepository.findByIdAndUser(defaultCategory.getId(), testUser))
            .thenReturn(Optional.of(defaultCategory));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            categoryService.deleteCategory(testUser, defaultCategory.getId())
        );
        assertEquals("Cannot delete default categories", exception.getMessage());
    }

    @Test
    void getCategoryByIdAndUser_ShouldReturnCategoryWhenExists() {
        // Given
        when(categoryRepository.findByIdAndUser(customCategory.getId(), testUser))
            .thenReturn(Optional.of(customCategory));

        // When
        Category result = categoryService.getCategoryByIdAndUser(customCategory.getId(), testUser);

        // Then
        assertEquals(customCategory, result);
    }

    @Test
    void getCategoryByIdAndUser_ShouldThrowExceptionWhenNotFound() {
        // Given
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.findByIdAndUser(categoryId, testUser))
            .thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
            categoryService.getCategoryByIdAndUser(categoryId, testUser)
        );
        assertEquals("Category not found", exception.getMessage());
    }
}