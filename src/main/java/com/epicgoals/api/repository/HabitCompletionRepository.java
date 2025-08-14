// ABOUT_ME: This file defines the repository interface for HabitCompletion data access operations
// ABOUT_ME: Provides methods for tracking daily habit completions with date range queries
package com.epicgoals.api.repository;

import com.epicgoals.api.entity.Habit;
import com.epicgoals.api.entity.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, UUID> {
    
    List<HabitCompletion> findByHabitAndDateBetween(Habit habit, LocalDate startDate, LocalDate endDate);
    
    Optional<HabitCompletion> findByHabitAndDate(Habit habit, LocalDate date);
    
    List<HabitCompletion> findByHabitInAndDateBetween(List<Habit> habits, LocalDate startDate, LocalDate endDate);
    
    void deleteByHabit(Habit habit);
}