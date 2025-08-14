// ABOUT_ME: This file handles HTTP requests for progress and weekly score operations
// ABOUT_ME: Provides REST endpoints for retrieving weekly scores and user progress tracking
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.WeeklyScoreDto;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ScoreService scoreService;

    @Autowired
    public ProgressController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping("/current-week")
    public ResponseEntity<WeeklyScoreDto> getCurrentWeekScore(@AuthenticationPrincipal User user) {
        WeeklyScoreDto currentWeekScore = scoreService.getCurrentWeekScore(user);
        return ResponseEntity.ok(currentWeekScore);
    }

    @GetMapping("/weekly-scores")
    public ResponseEntity<List<WeeklyScoreDto>> getWeeklyScores(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "12") int weeks) {
        
        // Limit weeks to reasonable range
        int limitedWeeks = Math.min(Math.max(weeks, 1), 52);
        
        List<WeeklyScoreDto> weeklyScores = scoreService.getHistoricalScores(user, limitedWeeks);
        return ResponseEntity.ok(weeklyScores);
    }

    @GetMapping("/week")
    public ResponseEntity<WeeklyScoreDto> getWeekScore(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        
        WeeklyScoreDto weekScore = scoreService.getWeekScore(user, weekStart);
        return ResponseEntity.ok(weekScore);
    }

    @PostMapping("/recalculate")
    public ResponseEntity<WeeklyScoreDto> recalculateCurrentWeek(@AuthenticationPrincipal User user) {
        scoreService.recalculateCurrentWeek(user);
        WeeklyScoreDto updatedScore = scoreService.getCurrentWeekScore(user);
        return ResponseEntity.ok(updatedScore);
    }
}