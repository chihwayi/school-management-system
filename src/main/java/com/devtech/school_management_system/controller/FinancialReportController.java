package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.FinancialReportDTO;
import com.devtech.school_management_system.service.FinancialReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/api/financial-reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class FinancialReportController {
    
    private final FinancialReportService financialReportService;

    public FinancialReportController(FinancialReportService financialReportService) {
        this.financialReportService = financialReportService;
    }

    @GetMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public FinancialReportDTO generateFinancialReport(
            @RequestParam String term,
            @RequestParam String academicYear,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return financialReportService.generateFinancialReport(term, academicYear, startDate, endDate);
    }
}