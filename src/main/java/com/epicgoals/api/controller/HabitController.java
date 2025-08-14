// ABOUT_ME: This file handles HTTP requests for habit management operations
// ABOUT_ME: Provides REST endpoints for habit CRUD operations and completion tracking with authentication
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.CreateHabitRequest;
import com.epicgoals.api.dto.HabitCompletionDto;
import com.epicgoals.api.dto.HabitDto;
import com.epicgoals.api.dto.UpdateHabitRequest;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    @Autowired
    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping
    public ResponseEntity<List<HabitDto>> getUserHabits(@AuthenticationPrincipal User user) {
        List<HabitDto> habits = habitService.getUserHabits(user);
        return ResponseEntity.ok(habits);
    }

    @PostMapping
    public ResponseEntity<HabitDto> createHabit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateHabitRequest request) {
        HabitDto habit = habitService.createHabit(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(habit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitDto> updateHabit(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateHabitRequest request) {
        HabitDto habit = habitService.updateHabit(user, id, request);
        return ResponseEntity.ok(habit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        habitService.deleteHabit(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/completions")
    public ResponseEntity<List<HabitCompletionDto>> getHabitCompletions(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<HabitCompletionDto> completions = habitService.getHabitCompletions(user, id, startDate, endDate);
        return ResponseEntity.ok(completions);
    }

    @PostMapping("/{id}/completions")
    public ResponseEntity<HabitCompletionDto> toggleHabitCompletion(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        HabitCompletionDto completion = habitService.toggleHabitCompletion(user, id, date);
        return ResponseEntity.ok(completion);
    }
}