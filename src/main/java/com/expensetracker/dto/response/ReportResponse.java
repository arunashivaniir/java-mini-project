package com.expensetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ReportResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyReport {
        private int month;
        private int year;
        private Double totalAmount;
        private Long totalTransactions;
        private List<ExpenseResponse> expenses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryReport {
        private Long categoryId;
        private String categoryName;
        private Double totalAmount;
        private Long transactionCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryReport {
        private Double totalExpenses;
        private Long totalTransactions;
        private Double averageTransactionAmount;
        private List<CategoryReport> categoryBreakdown;
    }
}
