package com.devtech.school_management_system.dto;

import java.math.BigDecimal;

public class FeeSettingDTO {
    private Long id;
    private String level;
    private BigDecimal amount;
    private String academicYear;
    private String term;
    private boolean active;
    
    // Constructors
    public FeeSettingDTO() {}
    
    public FeeSettingDTO(Long id, String level, BigDecimal amount, String academicYear, String term, boolean active) {
        this.id = id;
        this.level = level;
        this.amount = amount;
        this.academicYear = academicYear;
        this.term = term;
        this.active = active;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public String getTerm() {
        return term;
    }
    
    public void setTerm(String term) {
        this.term = term;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}