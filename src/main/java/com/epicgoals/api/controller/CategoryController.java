// ABOUT_ME: This file handles HTTP requests for category management operations
// ABOUT_ME: Provides REST endpoints for CRUD operations on user categories with proper authentication
package com.epicgoals.api.controller;

import com.epicgoals.api.dto.CategoryDto;
import com.epicgoals.api.dto.CreateCategoryRequest;
import com.epicgoals.api.dto.UpdateCategoryRequest;
import com.epicgoals.api.entity.User;
import com.epicgoals.api.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getUserCategories(@AuthenticationPrincipal User user) {
        List<CategoryDto> categories = categoryService.getUserCategories(user);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryDto category = categoryService.createCategory(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryDto category = categoryService.updateCategory(user, id, request);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        categoryService.deleteCategory(user, id);
        return ResponseEntity.noContent().build();
    }
}