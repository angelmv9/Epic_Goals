// ABOUT_ME: This file provides business logic for habit management operations  
// ABOUT_ME: Handles habit CRUD operations, completion tracking, and enforces 15-habit limit
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class HabitService {

    private static final int MAX_HABITS_PER_USER = 15;
    
    private final HabitRepository habitRepository;
    private final HabitCompletionRepository habitCompletionRepository;
    private final CategoryService categoryService;
    
    @Autowired
    public HabitService(HabitRepository habitRepository, 
                       HabitCompletionRepository habitCompletionRepository,
                       CategoryService categoryService) {
        this.habitRepository = habitRepository;
        this.habitCompletionRepository = habitCompletionRepository;
        this.categoryService = categoryService;
    }
    
    @Transactional(readOnly = true)
    public List<HabitDto> getUserHabits(User user) {
        List<Habit> habits = habitRepository.findByUserAndIsActiveTrueOrderByName(user);
        return habits.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public HabitDto createHabit(User user, CreateHabitRequest request) {
        // Check habit limit
        int currentHabitCount = habitRepository.countByUserAndIsActiveTrue(user);
        if (currentHabitCount >= MAX_HABITS_PER_USER) {
            throw new IllegalArgumentException("Cannot create habit. Maximum of " + MAX_HABITS_PER_USER + " habits allowed");
        }
        
        // Verify category belongs to user
        Category category = categoryService.getCategoryByIdAndUser(request.getCategoryId(), user);
        
        Habit habit = new Habit(user, category, request.getName(), request.getFrequency());
        Habit savedHabit = habitRepository.save(habit);
        return convertToDto(savedHabit);
    }
    
    public HabitDto updateHabit(User user, UUID habitId, UpdateHabitRequest request) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new EntityNotFoundException("Habit not found"));
        
        // Verify category belongs to user
        Category category = categoryService.getCategoryByIdAndUser(request.getCategoryId(), user);
        
        habit.setName(request.getName());
        habit.setCategory(category);
        habit.setFrequency(request.getFrequency());
        habit.setIsActive(request.getIsActive());
        
        Habit savedHabit = habitRepository.save(habit);
        return convertToDto(savedHabit);
    }
    
    public void deleteHabit(User user, UUID habitId) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new EntityNotFoundException("Habit not found"));
        
        // Delete all completions for this habit
        habitCompletionRepository.deleteByHabit(habit);
        
        // Delete the habit
        habitRepository.delete(habit);
    }
    
    @Transactional(readOnly = true)
    public List<HabitCompletionDto> getHabitCompletions(User user, UUID habitId, LocalDate startDate, LocalDate endDate) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new EntityNotFoundException("Habit not found"));
        
        List<HabitCompletion> completions = habitCompletionRepository.findByHabitAndDateBetween(habit, startDate, endDate);
        return completions.stream()
                .map(this::convertToCompletionDto)
                .collect(Collectors.toList());
    }
    
    public HabitCompletionDto toggleHabitCompletion(User user, UUID habitId, LocalDate date) {
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new EntityNotFoundException("Habit not found"));
        
        Optional<HabitCompletion> existingCompletion = habitCompletionRepository.findByHabitAndDate(habit, date);
        
        if (existingCompletion.isPresent()) {
            HabitCompletion completion = existingCompletion.get();
            completion.setCompleted(!completion.getCompleted());
            HabitCompletion savedCompletion = habitCompletionRepository.save(completion);
            return convertToCompletionDto(savedCompletion);
        } else {
            HabitCompletion newCompletion = new HabitCompletion(habit, date, true);
            HabitCompletion savedCompletion = habitCompletionRepository.save(newCompletion);
            return convertToCompletionDto(savedCompletion);
        }
    }
    
    @Transactional(readOnly = true)
    public Habit getHabitByIdAndUser(UUID habitId, User user) {
        return habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new EntityNotFoundException("Habit not found"));
    }
    
    private HabitDto convertToDto(Habit habit) {
        return new HabitDto(
                habit.getId(),
                habit.getName(),
                habit.getFrequency(),
                habit.getIsActive(),
                habit.getCategory().getId(),
                habit.getCategory().getName(),
                habit.getCreatedAt(),
                habit.getUpdatedAt()
        );
    }
    
    private HabitCompletionDto convertToCompletionDto(HabitCompletion completion) {
        return new HabitCompletionDto(
                completion.getId(),
                completion.getHabit().getId(),
                completion.getDate(),
                completion.getCompleted(),
                completion.getCreatedAt(),
                completion.getUpdatedAt()
        );
    }
}