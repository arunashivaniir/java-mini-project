package com.expensetracker.service;

import com.expensetracker.dto.request.ExpenseRequest;
import com.expensetracker.dto.response.ExpenseResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final SecurityContextService securityContextService;

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        User currentUser = securityContextService.getCurrentUser();

        Category category = categoryRepository
                .findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId() +
                        " for current user"));

        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .category(category)
                .user(currentUser)
                .build();

        Expense saved = expenseRepository.save(expense);
        log.info("Expense created: amount={}, category={}, user={}",
                saved.getAmount(), category.getName(), currentUser.getEmail());
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getAllExpenses(int page, int size, String sortBy, String sortDir) {
        Long userId = securityContextService.getCurrentUserId();
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return expenseRepository.findByUserIdOrderByDateDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesByDateRange(LocalDate startDate, LocalDate endDate,
                                                         int page, int size) {
        Long userId = securityContextService.getCurrentUserId();

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be before or equal to endDate");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return expenseRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesByCategory(Long categoryId, int page, int size) {
        Long userId = securityContextService.getCurrentUserId();

        // Validate category belongs to user
        categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return expenseRepository.findByUserIdAndCategoryId(userId, categoryId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        User currentUser = securityContextService.getCurrentUser();

        Expense expense = expenseRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));

        Category category = categoryRepository
                .findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId() +
                        " for current user"));

        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setCategory(category);

        Expense updated = expenseRepository.save(expense);
        log.info("Expense updated: id={}", id);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteExpense(Long id) {
        Long userId = securityContextService.getCurrentUserId();

        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));

        expenseRepository.delete(expense);
        log.info("Expense deleted: id={}", id);
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .date(expense.getDate())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .userId(expense.getUser().getId())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
