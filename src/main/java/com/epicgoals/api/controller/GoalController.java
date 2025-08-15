// ABOUT_ME: Controller handling HTTP requests for goal management operations
// ABOUT_ME: Provides REST endpoints for multi-timeframe goal CRUD operations with authentication
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.GoalCreateRequest;
import com.epicgoals.api.dto.GoalResponse;
import com.epicgoals.api.dto.GoalUpdateRequest;
import com.epicgoals.api.entity.GoalTimeframe;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getUserGoals(@AuthenticationPrincipal User user) {
        List<GoalResponse> goals = goalService.getUserGoals(user);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/timeframe/{timeframe}")
    public ResponseEntity<List<GoalResponse>> getUserGoalsByTimeframe(
            @AuthenticationPrincipal User user,
            @PathVariable GoalTimeframe timeframe) {
        List<GoalResponse> goals = goalService.getUserGoalsByTimeframe(user, timeframe);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<GoalResponse>> getChildGoals(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        List<GoalResponse> childGoals = goalService.getChildGoals(user, id);
        return ResponseEntity.ok(childGoals);
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody GoalCreateRequest request) {
        GoalResponse goal = goalService.createGoal(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody GoalUpdateRequest request) {
        GoalResponse goal = goalService.updateGoal(user, id, request);
        return ResponseEntity.ok(goal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        goalService.deleteGoal(user, id);
        return ResponseEntity.noContent().build();
    }
}