// ABOUT_ME: This file defines the repository interface for Category data access operations
// ABOUT_ME: Provides methods to find categories by user with proper data isolation
package com.epicgoals.api.repository;

import com.epicgoals.api.entity.Category;
import com.epicgoals.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    List<Category> findByUserOrderByName(User user);
    
    Optional<Category> findByIdAndUser(UUID id, User user);
    
    List<Category> findByUserAndIsDefaultTrue(User user);
    
    boolean existsByUserAndName(User user, String name);
}