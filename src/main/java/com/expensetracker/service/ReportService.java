package com.expensetracker.service;

import com.expensetracker.dto.response.ExpenseResponse;
import com.expensetracker.dto.response.ReportResponse;
import com.expensetracker.entity.Expense;
import com.expensetracker.exception.BadRequestException;
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
public class ReportService {

    private final ExpenseRepository expenseRepository;
    private final SecurityContextService securityContextService;

    @Transactional(readOnly = true)
    public ReportResponse.MonthlyReport getMonthlyReport(int month, int year) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must be between 1 and 12");
        }
        if (year < 2000 || year > 2100) {
            throw new BadRequestException("Invalid year provided");
        }

        Long userId = securityContextService.getCurrentUserId();
        List<Expense> expenses = expenseRepository.findByUserIdAndMonthAndYear(userId, month, year);

        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        List<ExpenseResponse> expenseResponses = expenses.stream()
                .map(this::mapExpenseToResponse)
                .collect(Collectors.toList());

        return ReportResponse.MonthlyReport.builder()
                .month(month)
                .year(year)
                .totalAmount(total)
                .totalTransactions((long) expenses.size())
                .expenses(expenseResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReportResponse.CategoryReport> getCategoryReport() {
        Long userId = securityContextService.getCurrentUserId();
        return expenseRepository.findCategoryWiseTotals(userId);
    }

    @Transactional(readOnly = true)
    public ReportResponse.SummaryReport getSummaryReport() {
        Long userId = securityContextService.getCurrentUserId();

        Double totalAmount = expenseRepository.sumAmountByUserId(userId);
        Long totalTransactions = expenseRepository.countByUserId(userId);
        List<ReportResponse.CategoryReport> categoryBreakdown =
                expenseRepository.findCategoryWiseTotals(userId);

        double total = totalAmount != null ? totalAmount : 0.0;
        long count = totalTransactions != null ? totalTransactions : 0L;
        double average = count > 0 ? total / count : 0.0;

        return ReportResponse.SummaryReport.builder()
                .totalExpenses(total)
                .totalTransactions(count)
                .averageTransactionAmount(Math.round(average * 100.0) / 100.0)
                .categoryBreakdown(categoryBreakdown)
                .build();
    }

    private ExpenseResponse mapExpenseToResponse(Expense expense) {
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
