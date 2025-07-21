package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.*;
import com.devtech.school_management_system.entity.FeePayment;
import com.devtech.school_management_system.service.FinancialReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
    
    @GetMapping("/student-payment-history")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentPaymentHistoryDTO> getAllStudentPaymentHistory() {
        return financialReportService.getAllStudentPaymentHistory();
    }
    
    @GetMapping("/student-payment-history/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentPaymentHistoryDTO> getStudentPaymentHistory(@PathVariable Long studentId) {
        return financialReportService.getStudentPaymentHistory(studentId);
    }
    
    @GetMapping("/payment-trends")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentTrendDTO> getPaymentTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return financialReportService.getPaymentTrends(startDate, endDate);
    }
    
    @GetMapping("/class-comparison")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ClassComparisonDTO> getClassComparison(@RequestParam String academicYear) {
        return financialReportService.getClassComparison(academicYear);
    }
    
    @GetMapping("/outstanding-payments")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FeePayment> getOutstandingPayments(
            @RequestParam String term,
            @RequestParam String academicYear) {
        return financialReportService.getOutstandingPayments(term, academicYear);
    }
    
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogDTO> getPaymentAuditLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return financialReportService.getPaymentAuditLogs(startDate, endDate);
    }
    
    @GetMapping(value = "/export/all-payments", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportAllPaymentsToExcel(
            @RequestParam String term,
            @RequestParam String academicYear) {
        byte[] excelBytes = financialReportService.exportAllPaymentsToExcel(term, academicYear);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_payments.xlsx")
                .body(excelBytes);
    }
    
    @GetMapping(value = "/export/student-history/{studentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportStudentPaymentHistory(@PathVariable Long studentId) {
        byte[] excelBytes = financialReportService.exportStudentPaymentHistoryToExcel(studentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_payment_history.xlsx")
                .body(excelBytes);
    }
}