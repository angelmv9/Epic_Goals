// ABOUT_ME: This file provides business logic for category management operations
// ABOUT_ME: Handles category CRUD operations, default category creation, and habit reassignment
package com.epicgoals.api.service;

import com.epicgoals.api.dto.CategoryDto;
import com.epicgoals.api.dto.CreateCategoryRequest;
import com.epicgoals.api.dto.UpdateCategoryRequest;
import com.epicgoals.api.entity.Category;
import com.epicgoals.api.entity.Habit;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.repository.CategoryRepository;
import com.epicgoals.api.repository.HabitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final HabitRepository habitRepository;
    
    @Autowired
    public CategoryService(CategoryRepository categoryRepository, HabitRepository habitRepository) {
        this.categoryRepository = categoryRepository;
        this.habitRepository = habitRepository;
    }
    
    public void createDefaultCategoriesForUser(User user) {
        String[] defaultCategoryNames = {"Career", "Health", "Family", "Finances", "Wisdom"};
        
        for (String categoryName : defaultCategoryNames) {
            if (!categoryRepository.existsByUserAndName(user, categoryName)) {
                Category category = new Category(user, categoryName, true);
                categoryRepository.save(category);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<CategoryDto> getUserCategories(User user) {
        List<Category> categories = categoryRepository.findByUserOrderByName(user);
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public CategoryDto createCategory(User user, CreateCategoryRequest request) {
        if (categoryRepository.existsByUserAndName(user, request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }
        
        Category category = new Category(user, request.getName(), false);
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }
    
    public CategoryDto updateCategory(User user, UUID categoryId, UpdateCategoryRequest request) {
        Category category = categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        
        if (category.getIsDefault()) {
            throw new IllegalArgumentException("Cannot modify default categories");
        }
        
        if (!category.getName().equals(request.getName()) && 
            categoryRepository.existsByUserAndName(user, request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }
        
        category.setName(request.getName());
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }
    
    public void deleteCategory(User user, UUID categoryId) {
        Category category = categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        
        if (category.getIsDefault()) {
            throw new IllegalArgumentException("Cannot delete default categories");
        }
        
        // Find or create "Uncategorized" category
        Category uncategorizedCategory = getOrCreateUncategorizedCategory(user);
        
        // Reassign habits to uncategorized
        List<Habit> habits = habitRepository.findByCategory(category);
        for (Habit habit : habits) {
            habit.setCategory(uncategorizedCategory);
            habitRepository.save(habit);
        }
        
        categoryRepository.delete(category);
    }
    
    @Transactional(readOnly = true)
    public Category getCategoryByIdAndUser(UUID categoryId, User user) {
        return categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
    
    private Category getOrCreateUncategorizedCategory(User user) {
        return categoryRepository.findByUserAndIsDefaultTrue(user).stream()
                .filter(category -> "Uncategorized".equals(category.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Category uncategorized = new Category(user, "Uncategorized", true);
                    return categoryRepository.save(uncategorized);
                });
    }
    
    private CategoryDto convertToDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getIsDefault(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}