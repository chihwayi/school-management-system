package com.devtech.school_management_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePaymentDTO {
    private Long studentId;
    private String term;
    private String month;
    private String academicYear;
    private BigDecimal monthlyFeeAmount;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;

    // Constructors
    public FeePaymentDTO() {}

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public BigDecimal getMonthlyFeeAmount() { return monthlyFeeAmount; }
    public void setMonthlyFeeAmount(BigDecimal monthlyFeeAmount) { this.monthlyFeeAmount = monthlyFeeAmount; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
}