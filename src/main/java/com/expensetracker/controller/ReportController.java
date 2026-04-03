package com.expensetracker.controller;

import com.expensetracker.dto.response.ApiResponse;
import com.expensetracker.dto.response.ReportResponse;
import com.expensetracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<ReportResponse.MonthlyReport>> getMonthlyReport(
            @RequestParam int month,
            @RequestParam int year) {
        ReportResponse.MonthlyReport report = reportService.getMonthlyReport(month, year);
        return ResponseEntity.ok(ApiResponse.success(
                "Monthly report for " + month + "/" + year, report));
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<ReportResponse.CategoryReport>>> getCategoryReport() {
        List<ReportResponse.CategoryReport> report = reportService.getCategoryReport();
        return ResponseEntity.ok(ApiResponse.success("Category-wise expense report", report));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ReportResponse.SummaryReport>> getSummaryReport() {
        ReportResponse.SummaryReport report = reportService.getSummaryReport();
        return ResponseEntity.ok(ApiResponse.success("Expense summary report", report));
    }
}
