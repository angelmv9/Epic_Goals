// ABOUT_ME: Unit tests for HabitService business logic and validation rules
// ABOUT_ME: Tests habit CRUD operations, 15-habit limit enforcement, and completion tracking
package com.epicgoals.api.service;

import com.epicgoals.api.dto.CreateHabitRequest;
import com.epicgoals.api.dto.HabitCompletionDto;
import com.epicgoals.api.dto.HabitDto;
import com.epicgoals.api.dto.UpdateHabitRequest;
import com.epicgoals.api.entity.Category;
import com.epicgoals.api.entity.Habit;
import com.epicgoals.api.entity.HabitCompletion;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.repository.HabitCompletionRepository;
import com.epicgoals.api.repository.HabitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCompletionRepository habitCompletionRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private HabitService habitService;

    private User testUser;
    private Category testCategory;
    private Habit testHabit;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "hashedPassword");
        testUser.setId(UUID.randomUUID());
        
        testCategory = new Category(testUser, "Health", true);
        testCategory.setId(UUID.randomUUID());
        
        testHabit = new Habit(testUser, testCategory, "Exercise", 5);
        testHabit.setId(UUID.randomUUID());
    }

    @Test
    void getUserHabits_ShouldReturnAllActiveUserHabits() {
        // Given
        List<Habit> habits = Arrays.asList(testHabit);
        when(habitRepository.findByUserAndIsActiveTrueOrderByName(testUser)).thenReturn(habits);

        // When
        List<HabitDto> result = habitService.getUserHabits(testUser);

        // Then
        assertEquals(1, result.size());
        assertEquals("Exercise", result.get(0).getName());
        assertEquals(5, result.get(0).getFrequency());
        assertEquals(testCategory.getId(), result.get(0).getCategoryId());
        assertEquals("Health", result.get(0).getCategoryName());
    }

    @Test
    void createHabit_ShouldCreateValidHabit() {
        // Given
        CreateHabitRequest request = new CreateHabitRequest("Running", testCategory.getId(), 3);
        when(habitRepository.countByUserAndIsActiveTrue(testUser)).thenReturn(5);
        when(categoryService.getCategoryByIdAndUser(testCategory.getId(), testUser))
            .thenReturn(testCategory);
        when(habitRepository.save(any(Habit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        HabitDto result = habitService.createHabit(testUser, request);

        // Then
        assertEquals("Running", result.getName());
        assertEquals(3, result.getFrequency());
        assertEquals(testCategory.getId(), result.getCategoryId());
        assertEquals("Health", result.getCategoryName());
        verify(habitRepository).save(any(Habit.class));
    }

    @Test
    void createHabit_ShouldEnforce15HabitLimit() {
        // Given
        CreateHabitRequest request = new CreateHabitRequest("NewHabit", testCategory.getId(), 3);
        when(habitRepository.countByUserAndIsActiveTrue(testUser)).thenReturn(15);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            habitService.createHabit(testUser, request)
        );
        assertEquals("Cannot create habit. Maximum of 15 habits allowed", exception.getMessage());
        verify(habitRepository, never()).save(any(Habit.class));
    }

    @Test
    void updateHabit_ShouldUpdateValidHabit() {
        // Given
        UpdateHabitRequest request = new UpdateHabitRequest("Updated Exercise", testCategory.getId(), 7, true);
        when(habitRepository.findByIdAndUser(testHabit.getId(), testUser))
            .thenReturn(Optional.of(testHabit));
        when(categoryService.getCategoryByIdAndUser(testCategory.getId(), testUser))
            .thenReturn(testCategory);
        when(habitRepository.save(any(Habit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        HabitDto result = habitService.updateHabit(testUser, testHabit.getId(), request);

        // Then
        assertEquals("Updated Exercise", result.getName());
        assertEquals(7, result.getFrequency());
        assertEquals(true, result.getIsActive());
        verify(habitRepository).save(testHabit);
    }

    @Test
    void updateHabit_ShouldThrowExceptionWhenHabitNotFound() {
        // Given
        UUID habitId = UUID.randomUUID();
        UpdateHabitRequest request = new UpdateHabitRequest("Updated", testCategory.getId(), 5, true);
        when(habitRepository.findByIdAndUser(habitId, testUser)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
            habitService.updateHabit(testUser, habitId, request)
        );
        assertEquals("Habit not found", exception.getMessage());
    }

    @Test
    void deleteHabit_ShouldDeleteHabitAndCompletions() {
        // Given
        when(habitRepository.findByIdAndUser(testHabit.getId(), testUser))
            .thenReturn(Optional.of(testHabit));

        // When
        habitService.deleteHabit(testUser, testHabit.getId());

        // Then
        verify(habitCompletionRepository).deleteByHabit(testHabit);
        verify(habitRepository).delete(testHabit);
    }

    @Test
    void getHabitCompletions_ShouldReturnCompletionsInRange() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 7);
        
        HabitCompletion completion1 = new HabitCompletion(testHabit, LocalDate.of(2024, 1, 1), true);
        completion1.setId(UUID.randomUUID());
        HabitCompletion completion2 = new HabitCompletion(testHabit, LocalDate.of(2024, 1, 3), true);
        completion2.setId(UUID.randomUUID());
        
        List<HabitCompletion> completions = Arrays.asList(completion1, completion2);
        
        when(habitRepository.findByIdAndUser(testHabit.getId(), testUser))
            .thenReturn(Optional.of(testHabit));
        when(habitCompletionRepository.findByHabitAndDateBetween(testHabit, startDate, endDate))
            .thenReturn(completions);

        // When
        List<HabitCompletionDto> result = habitService.getHabitCompletions(testUser, testHabit.getId(), startDate, endDate);

        // Then
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2024, 1, 1), result.get(0).getDate());
        assertEquals(LocalDate.of(2024, 1, 3), result.get(1).getDate());
        assertTrue(result.get(0).getCompleted());
        assertTrue(result.get(1).getCompleted());
    }

    @Test
    void toggleHabitCompletion_ShouldCreateNewCompletionWhenNotExists() {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(habitRepository.findByIdAndUser(testHabit.getId(), testUser))
            .thenReturn(Optional.of(testHabit));
        when(habitCompletionRepository.findByHabitAndDate(testHabit, date))
            .thenReturn(Optional.empty());
        when(habitCompletionRepository.save(any(HabitCompletion.class)))
            .thenAnswer(invocation -> {
                HabitCompletion completion = invocation.getArgument(0);
                completion.setId(UUID.randomUUID());
                return completion;
            });

        // When
        HabitCompletionDto result = habitService.toggleHabitCompletion(testUser, testHabit.getId(), date);

        // Then
        assertEquals(testHabit.getId(), result.getHabitId());
        assertEquals(date, result.getDate());
        assertTrue(result.getCompleted());
        verify(habitCompletionRepository).save(any(HabitCompletion.class));
    }

    @Test
    void toggleHabitCompletion_ShouldToggleExistingCompletion() {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 1);
        HabitCompletion existingCompletion = new HabitCompletion(testHabit, date, true);
        existingCompletion.setId(UUID.randomUUID());
        
        when(habitRepository.findByIdAndUser(testHabit.getId(), testUser))
            .thenReturn(Optional.of(testHabit));
        when(habitCompletionRepository.findByHabitAndDate(testHabit, date))
            .thenReturn(Optional.of(existingCompletion));
        when(habitCompletionRepository.save(any(HabitCompletion.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        HabitCompletionDto result = habitService.toggleHabitCompletion(testUser, testHabit.getId(), date);

        // Then
        assertEquals(testHabit.getId(), result.getHabitId());
        assertEquals(date, result.getDate());
        assertFalse(result.getCompleted()); // Should be toggled to false
        verify(habitCompletionRepository).save(existingCompletion);
        assertFalse(existingCompletion.getCompleted());
    }

    @Test
    void getHabitByIdAndUser_ShouldReturnHabitWhenExists() {
        // Given
        when(habitRepository.findByIdAndUser(testHabit.getId(), testUser))
            .thenReturn(Optional.of(testHabit));

        // When
        Habit result = habitService.getHabitByIdAndUser(testHabit.getId(), testUser);

        // Then
        assertEquals(testHabit, result);
    }

    @Test
    void getHabitByIdAndUser_ShouldThrowExceptionWhenNotFound() {
        // Given
        UUID habitId = UUID.randomUUID();
        when(habitRepository.findByIdAndUser(habitId, testUser))
            .thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
            habitService.getHabitByIdAndUser(habitId, testUser)
        );
        assertEquals("Habit not found", exception.getMessage());
    }
}