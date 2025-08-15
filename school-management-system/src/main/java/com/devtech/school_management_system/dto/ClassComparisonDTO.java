package com.devtech.school_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


public class ClassComparisonDTO {
    private String className;
    private Long totalStudents;
    private BigDecimal totalCollected;
    private BigDecimal totalOutstanding;
    private Double collectionRate;
    private BigDecimal averagePaymentPerStudent;

    public ClassComparisonDTO() {
    }

    public ClassComparisonDTO(String className, BigDecimal totalCollected, Long totalStudents, BigDecimal totalOutstanding, Double collectionRate, BigDecimal averagePaymentPerStudent) {
        this.className = className;
        this.totalCollected = totalCollected;
        this.totalStudents = totalStudents;
        this.totalOutstanding = totalOutstanding;
        this.collectionRate = collectionRate;
        this.averagePaymentPerStudent = averagePaymentPerStudent;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(BigDecimal totalCollected) {
        this.totalCollected = totalCollected;
    }

    public BigDecimal getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(BigDecimal totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public Double getCollectionRate() {
        return collectionRate;
    }

    public void setCollectionRate(Double collectionRate) {
        this.collectionRate = collectionRate;
    }

    public BigDecimal getAveragePaymentPerStudent() {
        return averagePaymentPerStudent;
    }

    public void setAveragePaymentPerStudent(BigDecimal averagePaymentPerStudent) {
        this.averagePaymentPerStudent = averagePaymentPerStudent;
    }
}