// ABOUT_ME: Repository interface for Goal entity providing database access methods
// ABOUT_ME: Includes custom queries for filtering by user, timeframe, and parent-child relationships
package com.epicgoals.api.repository;

import com.epicgoals.api.entity.Goal;
import com.epicgoals.api.entity.GoalTimeframe;
import com.epicgoals.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {
    
    List<Goal> findByUserAndIsActiveTrue(User user);
    
    List<Goal> findByUserAndTimeframeAndIsActiveTrue(User user, GoalTimeframe timeframe);
    
    List<Goal> findByUserAndParentGoalIdAndIsActiveTrue(User user, UUID parentGoalId);
    
    @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.id = :goalId AND g.isActive = true")
    Optional<Goal> findByUserAndIdAndIsActiveTrue(@Param("user") User user, @Param("goalId") UUID goalId);
    
    @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.parentGoalId IS NULL AND g.isActive = true")
    List<Goal> findTopLevelGoalsByUser(@Param("user") User user);
}