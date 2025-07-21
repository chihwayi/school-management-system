package com.devtech.school_management_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentReceiptDTO {
    private String studentName;
    private String className;
    private String term;
    private String month;
    private BigDecimal amountPaid;
    private BigDecimal balance;
    private LocalDate paymentDate;
    private BigDecimal monthlyFeeAmount;
    private String paymentStatus;

    // Constructors
    public PaymentReceiptDTO() {}

    public PaymentReceiptDTO(String studentName, String className, String term, String month, 
                           BigDecimal amountPaid, BigDecimal balance, LocalDate paymentDate) {
        this.studentName = studentName;
        this.className = className;
        this.term = term;
        this.month = month;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.paymentDate = paymentDate;
    }
    
    public PaymentReceiptDTO(String studentName, String className, String term, String month, 
                           BigDecimal amountPaid, BigDecimal balance, LocalDate paymentDate,
                           BigDecimal monthlyFeeAmount, String paymentStatus) {
        this.studentName = studentName;
        this.className = className;
        this.term = term;
        this.month = month;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.paymentDate = paymentDate;
        this.monthlyFeeAmount = monthlyFeeAmount;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public BigDecimal getMonthlyFeeAmount() { return monthlyFeeAmount; }
    public void setMonthlyFeeAmount(BigDecimal monthlyFeeAmount) { this.monthlyFeeAmount = monthlyFeeAmount; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}