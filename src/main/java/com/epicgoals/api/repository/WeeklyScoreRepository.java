// ABOUT_ME: This file defines the repository interface for WeeklyScore data access operations
// ABOUT_ME: Provides methods for querying weekly scores with user isolation and date ranges
package com.epicgoals.api.repository;

import com.epicgoals.api.entity.User;
import com.epicgoals.api.entity.WeeklyScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WeeklyScoreRepository extends JpaRepository<WeeklyScore, UUID> {
    
    Optional<WeeklyScore> findByUserAndWeekStartDate(User user, LocalDate weekStartDate);
    
    List<WeeklyScore> findByUserOrderByWeekStartDateDesc(User user);
    
    @Query("SELECT ws FROM WeeklyScore ws WHERE ws.user = :user AND ws.weekStartDate >= :startDate ORDER BY ws.weekStartDate DESC")
    List<WeeklyScore> findByUserAndWeekStartDateAfterOrderByWeekStartDateDesc(@Param("user") User user, @Param("startDate") LocalDate startDate);
    
    @Query("SELECT ws FROM WeeklyScore ws WHERE ws.user = :user ORDER BY ws.weekStartDate DESC")
    List<WeeklyScore> findTop12ByUserOrderByWeekStartDateDesc(@Param("user") User user);
    
    void deleteByUser(User user);
}