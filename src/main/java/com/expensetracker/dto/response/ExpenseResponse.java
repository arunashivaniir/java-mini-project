package com.expensetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    private Long id;
    private Double amount;
    private String description;
    private LocalDate date;
    private Long categoryId;
    private String categoryName;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
