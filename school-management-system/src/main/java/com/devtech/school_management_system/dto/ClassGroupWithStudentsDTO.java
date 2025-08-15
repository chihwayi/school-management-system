package com.devtech.school_management_system.dto;

import java.time.LocalDateTime;

public class ClassGroupWithStudentsDTO {
    private Long id;
    private String form;
    private String section;
    private String academicYear;
    private String level;
    private Integer classCapacity;
    private Long classTeacherId;
    private String classTeacherName;
    private Integer studentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ClassGroupWithStudentsDTO() {}
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getForm() {
        return form;
    }
    
    public void setForm(String form) {
        this.form = form;
    }
    
    public String getSection() {
        return section;
    }
    
    public void setSection(String section) {
        this.section = section;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public Integer getClassCapacity() {
        return classCapacity;
    }
    
    public void setClassCapacity(Integer classCapacity) {
        this.classCapacity = classCapacity;
    }
    
    public Long getClassTeacherId() {
        return classTeacherId;
    }
    
    public void setClassTeacherId(Long classTeacherId) {
        this.classTeacherId = classTeacherId;
    }
    
    public String getClassTeacherName() {
        return classTeacherName;
    }
    
    public void setClassTeacherName(String classTeacherName) {
        this.classTeacherName = classTeacherName;
    }
    
    public Integer getStudentCount() {
        return studentCount;
    }
    
    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getClassName() {
        return form + " " + section;
    }
}