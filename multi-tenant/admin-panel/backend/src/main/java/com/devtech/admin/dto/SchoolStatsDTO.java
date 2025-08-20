package com.devtech.admin.dto;

import java.time.LocalDateTime;

public class SchoolStatsDTO {
    private String schoolId;
    private String name;
    private String subdomain;
    private String planType;
    private String status;
    private Long totalUsers;
    private Long totalStudents;
    private Long totalTeachers;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;

    public SchoolStatsDTO() {}

    public SchoolStatsDTO(String schoolId, String name, String subdomain, String planType, String status) {
        this.schoolId = schoolId;
        this.name = name;
        this.subdomain = subdomain;
        this.planType = planType;
        this.status = status;
    }

    // Getters and Setters
    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(Long totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}








