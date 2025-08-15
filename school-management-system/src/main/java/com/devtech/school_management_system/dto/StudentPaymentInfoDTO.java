package com.devtech.school_management_system.dto;

import com.devtech.school_management_system.enums.PaymentStatus;

import java.math.BigDecimal;

public class StudentPaymentInfoDTO {
    private Long studentId;
    private String studentName;
    private String className;
    private BigDecimal amountPaid;
    private BigDecimal balance;
    private PaymentStatus paymentStatus;

    // Constructors
    public StudentPaymentInfoDTO() {}

    public StudentPaymentInfoDTO(Long studentId, String studentName, String className, 
                               BigDecimal amountPaid, BigDecimal balance, PaymentStatus paymentStatus) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = className;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
}