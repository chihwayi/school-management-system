package com.devtech.school_management_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyPaymentSummaryDTO {
    private LocalDate date;
    private BigDecimal totalAmount;
    private Long totalTransactions;

    // Constructors
    public DailyPaymentSummaryDTO() {}

    public DailyPaymentSummaryDTO(LocalDate date, BigDecimal totalAmount, Long totalTransactions) {
        this.date = date;
        this.totalAmount = totalAmount;
        this.totalTransactions = totalTransactions;
    }

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }
}