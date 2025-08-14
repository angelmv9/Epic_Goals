// ABOUT_ME: This file defines the repository interface for Habit data access operations
// ABOUT_ME: Includes methods for habit management with user isolation and counting active habits
package com.epicgoals.api.repository;

import com.epicgoals.api.entity.Category;
import com.epicgoals.api.entity.Habit;
import com.epicgoals.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HabitRepository extends JpaRepository<Habit, UUID> {
    
    List<Habit> findByUserAndIsActiveTrueOrderByName(User user);
    
    Optional<Habit> findByIdAndUser(UUID id, User user);
    
    List<Habit> findByCategory(Category category);
    
    int countByUserAndIsActiveTrue(User user);
    
    @Modifying
    @Query("UPDATE Habit h SET h.category = :newCategory WHERE h.category = :oldCategory")
    void reassignHabitsToCategory(@Param("oldCategory") Category oldCategory, @Param("newCategory") Category newCategory);
}