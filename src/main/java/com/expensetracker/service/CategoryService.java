package com.expensetracker.service;

import com.expensetracker.dto.request.CategoryRequest;
import com.expensetracker.dto.response.CategoryResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final SecurityContextService securityContextService;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        User currentUser = securityContextService.getCurrentUser();

        if (categoryRepository.existsByNameAndUserId(request.getName(), currentUser.getId())) {
            throw new BadRequestException(
                    "Category '" + request.getName() + "' already exists for this user");
        }

        Category category = Category.builder()
                .name(request.getName())
                .user(currentUser)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Category created: {} for user: {}", saved.getName(), currentUser.getEmail());
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        Long userId = securityContextService.getCurrentUserId();
        return categoryRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Long userId = securityContextService.getCurrentUserId();

        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByNameAndUserId(request.getName(), userId)) {
            throw new BadRequestException(
                    "Category '" + request.getName() + "' already exists for this user");
        }

        category.setName(request.getName());
        Category updated = categoryRepository.save(category);
        log.info("Category updated: id={}", id);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Long userId = securityContextService.getCurrentUserId();

        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        // Prevent deletion if expenses exist under this category
        if (!expenseRepository.findByCategoryId(id).isEmpty()) {
            throw new BadRequestException(
                    "Cannot delete category '" + category.getName() +
                    "' because it has existing expenses. Reassign or delete those expenses first.");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: id={}", id);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .userId(category.getUser().getId())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
