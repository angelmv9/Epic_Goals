// ABOUT_ME: Repository interface for User entity database operations
// ABOUT_ME: Provides methods to find users by email and check existence
package com.epicgoals.api.repository;

import com.epicgoals.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}