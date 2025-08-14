// ABOUT_ME: This file provides business logic for weekly score calculation and management
// ABOUT_ME: Handles habit completion scoring with caching and historical preservation
package com.epicgoals.api.service;

import com.epicgoals.api.dto.WeeklyScoreDto;
import com.epicgoals.api.entity.Habit;
import com.epicgoals.api.entity.HabitCompletion;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.entity.WeeklyScore;
import com.epicgoals.api.repository.HabitCompletionRepository;
import com.epicgoals.api.repository.HabitRepository;
import com.epicgoals.api.repository.WeeklyScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScoreService {

    private final WeeklyScoreRepository weeklyScoreRepository;
    private final HabitRepository habitRepository;
    private final HabitCompletionRepository habitCompletionRepository;

    @Autowired
    public ScoreService(WeeklyScoreRepository weeklyScoreRepository, 
                       HabitRepository habitRepository,
                       HabitCompletionRepository habitCompletionRepository) {
        this.weeklyScoreRepository = weeklyScoreRepository;
        this.habitRepository = habitRepository;
        this.habitCompletionRepository = habitCompletionRepository;
    }

    @Cacheable(value = "currentWeekScores", key = "#user.id")
    public WeeklyScoreDto getCurrentWeekScore(User user) {
        LocalDate weekStart = getWeekStart(LocalDate.now());
        return calculateWeekScore(user, weekStart);
    }

    public WeeklyScoreDto getWeekScore(User user, LocalDate weekStart) {
        // Ensure we're using the actual week start (Monday)
        LocalDate actualWeekStart = getWeekStart(weekStart);
        
        // Check if we already have a calculated score for this week
        Optional<WeeklyScore> existingScore = weeklyScoreRepository.findByUserAndWeekStartDate(user, actualWeekStart);
        if (existingScore.isPresent()) {
            return convertToDto(existingScore.get());
        }
        
        // Calculate and store new score
        return calculateWeekScore(user, actualWeekStart);
    }

    @Transactional(readOnly = true)
    public List<WeeklyScoreDto> getHistoricalScores(User user, int weeks) {
        LocalDate startDate = getWeekStart(LocalDate.now().minusWeeks(weeks - 1));
        List<WeeklyScore> scores = weeklyScoreRepository.findByUserAndWeekStartDateAfterOrderByWeekStartDateDesc(user, startDate);
        return scores.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "currentWeekScores", key = "#user.id")
    public void recalculateCurrentWeek(User user) {
        LocalDate weekStart = getWeekStart(LocalDate.now());
        calculateAndStoreWeekScore(user, weekStart);
    }

    @CacheEvict(value = "currentWeekScores", key = "#user.id")
    public void recalculateOnHabitChange(User user) {
        // Recalculate current week when habits are added/removed/modified
        recalculateCurrentWeek(user);
    }

    private WeeklyScoreDto calculateWeekScore(User user, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6); // Sunday
        
        // Get all active habits for the user
        List<Habit> activeHabits = habitRepository.findByUserAndIsActiveTrueOrderByName(user);
        
        if (activeHabits.isEmpty()) {
            // If no habits, return 0 score
            WeeklyScore weeklyScore = new WeeklyScore(user, weekStart, 0, 0, 0);
            WeeklyScore savedScore = weeklyScoreRepository.save(weeklyScore);
            return convertToDto(savedScore);
        }

        // Get all completions for the week
        List<HabitCompletion> completions = habitCompletionRepository.findByHabitInAndDateBetween(
                activeHabits, weekStart, weekEnd);

        // Calculate score for each habit
        double totalScore = 0.0;
        int totalExpectedCompletions = 0;
        int totalActualCompletions = 0;

        for (Habit habit : activeHabits) {
            int expectedCompletions = Math.min(habit.getFrequency(), 7); // Max 7 days in a week
            int actualCompletions = (int) completions.stream()
                    .filter(c -> c.getHabit().getId().equals(habit.getId()) && c.getCompleted())
                    .count();

            totalExpectedCompletions += expectedCompletions;
            totalActualCompletions += actualCompletions;
            
            // Individual habit score: (actual / expected) * 100
            double habitScore = expectedCompletions > 0 ? 
                    (double) actualCompletions / expectedCompletions * 100.0 : 0.0;
            totalScore += habitScore;
        }

        // Overall score: average of all habit scores
        int overallScore = activeHabits.size() > 0 ? 
                (int) Math.round(totalScore / activeHabits.size()) : 0;

        // Store the calculated score
        WeeklyScore weeklyScore = new WeeklyScore(user, weekStart, overallScore, 
                totalActualCompletions, totalExpectedCompletions);
        
        // Check if score already exists for this week, update if so
        Optional<WeeklyScore> existingScore = weeklyScoreRepository.findByUserAndWeekStartDate(user, weekStart);
        if (existingScore.isPresent()) {
            WeeklyScore existing = existingScore.get();
            existing.setScore(overallScore);
            existing.setCompletedHabits(totalActualCompletions);
            existing.setTotalHabits(totalExpectedCompletions);
            WeeklyScore savedScore = weeklyScoreRepository.save(existing);
            return convertToDto(savedScore);
        } else {
            WeeklyScore savedScore = weeklyScoreRepository.save(weeklyScore);
            return convertToDto(savedScore);
        }
    }

    private void calculateAndStoreWeekScore(User user, LocalDate weekStart) {
        calculateWeekScore(user, weekStart);
    }

    private LocalDate getWeekStart(LocalDate date) {
        // Get Monday of the week containing the given date
        return date.with(DayOfWeek.MONDAY);
    }

    private WeeklyScoreDto convertToDto(WeeklyScore weeklyScore) {
        return new WeeklyScoreDto(
                weeklyScore.getId(),
                weeklyScore.getWeekStartDate(),
                weeklyScore.getScore(),
                weeklyScore.getCompletedHabits(),
                weeklyScore.getTotalHabits(),
                weeklyScore.getCalculatedAt()
        );
    }
}