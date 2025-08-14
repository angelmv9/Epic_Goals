// ABOUT_ME: Unit tests for ScoreService business logic and scoring calculations
// ABOUT_ME: Tests weekly score calculation, caching, and historical data preservation
package com.epicgoals.api.service;

import com.epicgoals.api.dto.WeeklyScoreDto;
import com.epicgoals.api.entity.*;
import com.epicgoals.api.repository.HabitCompletionRepository;
import com.epicgoals.api.repository.HabitRepository;
import com.epicgoals.api.repository.WeeklyScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {

    @Mock
    private WeeklyScoreRepository weeklyScoreRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCompletionRepository habitCompletionRepository;

    @InjectMocks
    private ScoreService scoreService;

    private User testUser;
    private Category testCategory;
    private Habit habit1;
    private Habit habit2;
    private LocalDate currentWeekStart;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "hashedPassword");
        testUser.setId(UUID.randomUUID());
        
        testCategory = new Category(testUser, "Health", true);
        testCategory.setId(UUID.randomUUID());
        
        habit1 = new Habit(testUser, testCategory, "Exercise", 5); // 5 times per week
        habit1.setId(UUID.randomUUID());
        
        habit2 = new Habit(testUser, testCategory, "Meditation", 7); // Daily
        habit2.setId(UUID.randomUUID());
        
        // Get Monday of current week
        currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
    }

    @Test
    void getCurrentWeekScore_WithNoHabits_ShouldReturnZeroScore() {
        // Given
        when(habitRepository.findByUserAndIsActiveTrueOrderByName(testUser))
            .thenReturn(Collections.emptyList());
        when(weeklyScoreRepository.save(any(WeeklyScore.class)))
            .thenAnswer(invocation -> {
                WeeklyScore score = invocation.getArgument(0);
                score.setId(UUID.randomUUID());
                return score;
            });

        // When
        WeeklyScoreDto result = scoreService.getCurrentWeekScore(testUser);

        // Then
        assertEquals(0, result.getScore());
        assertEquals(0, result.getCompletedHabits());
        assertEquals(0, result.getTotalHabits());
        assertEquals(currentWeekStart, result.getWeekStartDate());
    }

    @Test
    void getCurrentWeekScore_WithHabitsAndCompletions_ShouldCalculateCorrectScore() {
        // Given
        List<Habit> habits = Arrays.asList(habit1, habit2);
        when(habitRepository.findByUserAndIsActiveTrueOrderByName(testUser))
            .thenReturn(habits);

        // Create completions: habit1 completed 3/5 times, habit2 completed 6/7 times
        List<HabitCompletion> completions = Arrays.asList(
            new HabitCompletion(habit1, currentWeekStart, true),
            new HabitCompletion(habit1, currentWeekStart.plusDays(1), true),
            new HabitCompletion(habit1, currentWeekStart.plusDays(2), true),
            new HabitCompletion(habit2, currentWeekStart, true),
            new HabitCompletion(habit2, currentWeekStart.plusDays(1), true),
            new HabitCompletion(habit2, currentWeekStart.plusDays(2), true),
            new HabitCompletion(habit2, currentWeekStart.plusDays(3), true),
            new HabitCompletion(habit2, currentWeekStart.plusDays(4), true),
            new HabitCompletion(habit2, currentWeekStart.plusDays(5), true)
        );
        
        when(habitCompletionRepository.findByHabitInAndDateBetween(
            eq(habits), eq(currentWeekStart), eq(currentWeekStart.plusDays(6))))
            .thenReturn(completions);

        when(weeklyScoreRepository.save(any(WeeklyScore.class)))
            .thenAnswer(invocation -> {
                WeeklyScore score = invocation.getArgument(0);
                score.setId(UUID.randomUUID());
                return score;
            });

        when(weeklyScoreRepository.findByUserAndWeekStartDate(testUser, currentWeekStart))
            .thenReturn(Optional.empty());

        // When
        WeeklyScoreDto result = scoreService.getCurrentWeekScore(testUser);

        // Then
        // habit1: 3/5 = 60%, habit2: 6/7 = 85.7%, average = (60 + 86) / 2 = 73
        assertEquals(73, result.getScore());
        assertEquals(9, result.getCompletedHabits()); // 3 + 6
        assertEquals(12, result.getTotalHabits()); // 5 + 7
        assertEquals(currentWeekStart, result.getWeekStartDate());
    }

    @Test
    void getCurrentWeekScore_WithExistingScore_ShouldReturnCachedScore() {
        // Given
        WeeklyScore existingScore = new WeeklyScore(testUser, currentWeekStart, 85, 10, 12);
        existingScore.setId(UUID.randomUUID());
        
        when(weeklyScoreRepository.findByUserAndWeekStartDate(testUser, currentWeekStart))
            .thenReturn(Optional.of(existingScore));

        // When
        WeeklyScoreDto result = scoreService.getWeekScore(testUser, currentWeekStart);

        // Then
        assertEquals(85, result.getScore());
        assertEquals(10, result.getCompletedHabits());
        assertEquals(12, result.getTotalHabits());
        assertEquals(currentWeekStart, result.getWeekStartDate());
        
        // Verify no new calculation was triggered
        verify(habitRepository, never()).findByUserAndIsActiveTrueOrderByName(testUser);
    }

    @Test
    void getHistoricalScores_ShouldReturnScoresInDescendingOrder() {
        // Given
        int weeksToFetch = 4;
        LocalDate startDate = currentWeekStart.minusWeeks(weeksToFetch - 1);
        
        List<WeeklyScore> historicalScores = Arrays.asList(
            new WeeklyScore(testUser, currentWeekStart, 90, 11, 12),
            new WeeklyScore(testUser, currentWeekStart.minusWeeks(1), 85, 10, 12),
            new WeeklyScore(testUser, currentWeekStart.minusWeeks(2), 80, 9, 12),
            new WeeklyScore(testUser, currentWeekStart.minusWeeks(3), 75, 8, 12)
        );
        
        historicalScores.forEach(score -> score.setId(UUID.randomUUID()));
        
        when(weeklyScoreRepository.findByUserAndWeekStartDateAfterOrderByWeekStartDateDesc(testUser, startDate))
            .thenReturn(historicalScores);

        // When
        List<WeeklyScoreDto> result = scoreService.getHistoricalScores(testUser, weeksToFetch);

        // Then
        assertEquals(4, result.size());
        assertEquals(90, result.get(0).getScore()); // Most recent first
        assertEquals(85, result.get(1).getScore());
        assertEquals(80, result.get(2).getScore());
        assertEquals(75, result.get(3).getScore()); // Oldest last
    }

    @Test
    void calculateScore_WithPerfectCompletion_ShouldReturn100() {
        // Given
        List<Habit> habits = Arrays.asList(habit1); // 5 times per week
        when(habitRepository.findByUserAndIsActiveTrueOrderByName(testUser))
            .thenReturn(habits);

        // Perfect completion: 5/5 times
        List<HabitCompletion> completions = Arrays.asList(
            new HabitCompletion(habit1, currentWeekStart, true),
            new HabitCompletion(habit1, currentWeekStart.plusDays(1), true),
            new HabitCompletion(habit1, currentWeekStart.plusDays(2), true),
            new HabitCompletion(habit1, currentWeekStart.plusDays(3), true),
            new HabitCompletion(habit1, currentWeekStart.plusDays(4), true)
        );
        
        when(habitCompletionRepository.findByHabitInAndDateBetween(
            eq(habits), eq(currentWeekStart), eq(currentWeekStart.plusDays(6))))
            .thenReturn(completions);

        when(weeklyScoreRepository.save(any(WeeklyScore.class)))
            .thenAnswer(invocation -> {
                WeeklyScore score = invocation.getArgument(0);
                score.setId(UUID.randomUUID());
                return score;
            });

        when(weeklyScoreRepository.findByUserAndWeekStartDate(testUser, currentWeekStart))
            .thenReturn(Optional.empty());

        // When
        WeeklyScoreDto result = scoreService.getCurrentWeekScore(testUser);

        // Then
        assertEquals(100, result.getScore()); // Perfect score
        assertEquals(5, result.getCompletedHabits());
        assertEquals(5, result.getTotalHabits());
    }

    @Test
    void calculateScore_WithNoCompletions_ShouldReturnZero() {
        // Given
        List<Habit> habits = Arrays.asList(habit1, habit2);
        when(habitRepository.findByUserAndIsActiveTrueOrderByName(testUser))
            .thenReturn(habits);

        // No completions
        when(habitCompletionRepository.findByHabitInAndDateBetween(
            eq(habits), eq(currentWeekStart), eq(currentWeekStart.plusDays(6))))
            .thenReturn(Collections.emptyList());

        when(weeklyScoreRepository.save(any(WeeklyScore.class)))
            .thenAnswer(invocation -> {
                WeeklyScore score = invocation.getArgument(0);
                score.setId(UUID.randomUUID());
                return score;
            });

        when(weeklyScoreRepository.findByUserAndWeekStartDate(testUser, currentWeekStart))
            .thenReturn(Optional.empty());

        // When
        WeeklyScoreDto result = scoreService.getCurrentWeekScore(testUser);

        // Then
        assertEquals(0, result.getScore()); // Zero score
        assertEquals(0, result.getCompletedHabits());
        assertEquals(12, result.getTotalHabits()); // 5 + 7 expected
    }

    @Test
    void recalculateCurrentWeek_ShouldUpdateExistingScore() {
        // Given
        List<Habit> habits = Arrays.asList(habit1);
        when(habitRepository.findByUserAndIsActiveTrueOrderByName(testUser))
            .thenReturn(habits);

        WeeklyScore existingScore = new WeeklyScore(testUser, currentWeekStart, 60, 3, 5);
        existingScore.setId(UUID.randomUUID());
        
        when(weeklyScoreRepository.findByUserAndWeekStartDate(testUser, currentWeekStart))
            .thenReturn(Optional.of(existingScore));

        // New completions show improved performance
        List<HabitCompletion> newCompletions = Arrays.asList(
            new HabitCompletion(habit1, currentWeekStart, true),
            new HabitCompletion(habit1, currentWeekStart.plusDays(1), true),
            new HabitCompletion(habit1, currentWeekStart.plusDays(2), true),
            new HabitCompletion(habit1, currentWeekStart.plusDays(3), true) // 4/5 now
        );
        
        when(habitCompletionRepository.findByHabitInAndDateBetween(
            eq(habits), eq(currentWeekStart), eq(currentWeekStart.plusDays(6))))
            .thenReturn(newCompletions);

        when(weeklyScoreRepository.save(any(WeeklyScore.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        scoreService.recalculateCurrentWeek(testUser);

        // Then
        verify(weeklyScoreRepository).save(argThat(score -> 
            score.getScore() == 80 && // 4/5 = 80%
            score.getCompletedHabits() == 4 &&
            score.getTotalHabits() == 5
        ));
    }

    @Test
    void getWeekScore_ShouldNormalizeToMondayStart() {
        // Given - Ask for a Wednesday, should normalize to Monday
        LocalDate wednesday = LocalDate.now().with(DayOfWeek.WEDNESDAY);
        LocalDate expectedMonday = wednesday.with(DayOfWeek.MONDAY);

        List<Habit> habits = Arrays.asList(habit1);
        when(habitRepository.findByUserAndIsActiveTrueOrderByName(testUser))
            .thenReturn(habits);

        when(habitCompletionRepository.findByHabitInAndDateBetween(
            eq(habits), eq(expectedMonday), eq(expectedMonday.plusDays(6))))
            .thenReturn(Collections.emptyList());

        when(weeklyScoreRepository.findByUserAndWeekStartDate(testUser, expectedMonday))
            .thenReturn(Optional.empty());

        when(weeklyScoreRepository.save(any(WeeklyScore.class)))
            .thenAnswer(invocation -> {
                WeeklyScore score = invocation.getArgument(0);
                score.setId(UUID.randomUUID());
                return score;
            });

        // When
        WeeklyScoreDto result = scoreService.getWeekScore(testUser, wednesday);

        // Then
        assertEquals(expectedMonday, result.getWeekStartDate());
        verify(weeklyScoreRepository, atLeast(1)).findByUserAndWeekStartDate(testUser, expectedMonday);
    }
}