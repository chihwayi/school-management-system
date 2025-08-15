package com.devtech.school_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


public class PaymentTrendDTO {
    private LocalDate date;
    private BigDecimal totalAmount;
    private Integer transactionCount;

    public PaymentTrendDTO() {
    }

    public PaymentTrendDTO(LocalDate date, BigDecimal totalAmount, Integer transactionCount) {
        this.date = date;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }
}