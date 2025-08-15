package com.devtech.school_management_system.dto;

import com.devtech.school_management_system.enums.AssessmentType;

import java.time.LocalDate;

public class AssessmentDTO {
    private Long studentId;
    private Long subjectId;
    private String title;
    private LocalDate date;
    private Double score;
    private Double maxScore;
    private AssessmentType type;
    private String term;
    private String academicYear;

    public AssessmentDTO() {
    }

    public AssessmentDTO(Long studentId, Long subjectId, String title, LocalDate date, Double score, Double maxScore, AssessmentType type, String term, String academicYear) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.title = title;
        this.date = date;
        this.score = score;
        this.maxScore = maxScore;
        this.type = type;
        this.term = term;
        this.academicYear = academicYear;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public AssessmentType getType() {
        return type;
    }

    public void setType(AssessmentType type) {
        this.type = type;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
}
