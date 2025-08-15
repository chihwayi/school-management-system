package com.devtech.school_management_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FinancialReportDTO {
    private LocalDate reportDate;
    private String term;
    private String academicYear;
    private BigDecimal totalExpectedRevenue;
    private BigDecimal totalCollectedAmount;
    private BigDecimal totalOutstandingAmount;
    private List<ClassFinancialSummaryDTO> classSummaries;
    private List<DailyPaymentSummaryDTO> dailySummaries;

    // Constructors
    public FinancialReportDTO() {}

    // Getters and Setters
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public BigDecimal getTotalExpectedRevenue() { return totalExpectedRevenue; }
    public void setTotalExpectedRevenue(BigDecimal totalExpectedRevenue) { this.totalExpectedRevenue = totalExpectedRevenue; }

    public BigDecimal getTotalCollectedAmount() { return totalCollectedAmount; }
    public void setTotalCollectedAmount(BigDecimal totalCollectedAmount) { this.totalCollectedAmount = totalCollectedAmount; }

    public BigDecimal getTotalOutstandingAmount() { return totalOutstandingAmount; }
    public void setTotalOutstandingAmount(BigDecimal totalOutstandingAmount) { this.totalOutstandingAmount = totalOutstandingAmount; }

    public List<ClassFinancialSummaryDTO> getClassSummaries() { return classSummaries; }
    public void setClassSummaries(List<ClassFinancialSummaryDTO> classSummaries) { this.classSummaries = classSummaries; }

    public List<DailyPaymentSummaryDTO> getDailySummaries() { return dailySummaries; }
    public void setDailySummaries(List<DailyPaymentSummaryDTO> dailySummaries) { this.dailySummaries = dailySummaries; }
}