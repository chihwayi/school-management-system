package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.*;
import com.devtech.school_management_system.entity.FeePayment;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.service.FeePaymentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/fee-payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeePaymentController {
    
    private final FeePaymentService feePaymentService;

    public FeePaymentController(FeePaymentService feePaymentService) {
        this.feePaymentService = feePaymentService;
    }

    @PostMapping("/record")
    @PreAuthorize("hasRole('CLERK')")
    public PaymentReceiptDTO recordPayment(@RequestBody FeePaymentDTO paymentDTO) {
        return feePaymentService.recordPayment(paymentDTO);
    }

    @GetMapping("/status/class/{form}/{section}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<PaymentStatusSummaryDTO> getPaymentStatusByClass(
            @PathVariable String form, 
            @PathVariable String section) {
        return feePaymentService.getPaymentStatusByClass(form, section);
    }

    @GetMapping("/daily-summary/{date}")
    @PreAuthorize("hasRole('ADMIN')")
    public DailyPaymentSummaryDTO getDailyPaymentSummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return feePaymentService.getDailyPaymentSummary(date);
    }

    @GetMapping("/student/{studentId}/term/{term}/year/{academicYear}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<FeePayment> getStudentPayments(
            @PathVariable Long studentId,
            @PathVariable String term,
            @PathVariable String academicYear) {
        return feePaymentService.getStudentPayments(studentId, term, academicYear);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<FeePayment> getPaymentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return feePaymentService.getPaymentsByDate(date);
    }
    
    @GetMapping("/search-students")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<Student> searchStudentsByName(@RequestParam String query) {
        return feePaymentService.searchStudentsByName(query);
    }
    
    @PostMapping("/fix-payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public String fixPaymentStatus() {
        feePaymentService.fixPaymentStatusForCompletedPayments();
        return "Payment statuses have been fixed successfully";
    }
    
    @GetMapping("/fix-student-payment/{studentName}")
    @PreAuthorize("hasRole('ADMIN')")
    public String fixStudentPayment(@PathVariable String studentName) {
        return feePaymentService.fixStudentPaymentByName(studentName);
    }
}