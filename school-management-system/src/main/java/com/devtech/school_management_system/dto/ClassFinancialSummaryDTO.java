package com.devtech.school_management_system.dto;

import java.math.BigDecimal;

public class ClassFinancialSummaryDTO {
    private String className;
    private Long totalStudents;
    private Long fullPayments;
    private Long partPayments;
    private Long nonPayers;
    private BigDecimal totalCollected;
    private BigDecimal totalOutstanding;

    // Constructors
    public ClassFinancialSummaryDTO() {}

    public ClassFinancialSummaryDTO(String className, Long totalStudents, Long fullPayments, 
                                  Long partPayments, Long nonPayers, BigDecimal totalCollected, 
                                  BigDecimal totalOutstanding) {
        this.className = className;
        this.totalStudents = totalStudents;
        this.fullPayments = fullPayments;
        this.partPayments = partPayments;
        this.nonPayers = nonPayers;
        this.totalCollected = totalCollected;
        this.totalOutstanding = totalOutstanding;
    }

    // Getters and Setters
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Long totalStudents) { this.totalStudents = totalStudents; }

    public Long getFullPayments() { return fullPayments; }
    public void setFullPayments(Long fullPayments) { this.fullPayments = fullPayments; }

    public Long getPartPayments() { return partPayments; }
    public void setPartPayments(Long partPayments) { this.partPayments = partPayments; }

    public Long getNonPayers() { return nonPayers; }
    public void setNonPayers(Long nonPayers) { this.nonPayers = nonPayers; }

    public BigDecimal getTotalCollected() { return totalCollected; }
    public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }

    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
    public void setTotalOutstanding(BigDecimal totalOutstanding) { this.totalOutstanding = totalOutstanding; }
}