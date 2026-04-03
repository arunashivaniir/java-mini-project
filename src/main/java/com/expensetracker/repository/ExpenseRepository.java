package com.expensetracker.repository;

import com.expensetracker.dto.response.ReportResponse;
import com.expensetracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);

    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    List<Expense> findByCategoryId(Long categoryId);

    // Filter by date range
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate ORDER BY e.date DESC")
    Page<Expense> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    // Filter by category
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId ORDER BY e.date DESC")
    Page<Expense> findByUserIdAndCategoryId(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    // Monthly report
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year ORDER BY e.date DESC")
    List<Expense> findByUserIdAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    // Category-wise totals
    @Query("SELECT new com.expensetracker.dto.response.ReportResponse$CategoryReport(e.category.id, e.category.name, SUM(e.amount), COUNT(e)) " +
            "FROM Expense e WHERE e.user.id = :userId GROUP BY e.category.id, e.category.name ORDER BY SUM(e.amount) DESC")
    List<ReportResponse.CategoryReport> findCategoryWiseTotals(@Param("userId") Long userId);

    // Summary totals
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    Double sumAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
