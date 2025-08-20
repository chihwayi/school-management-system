package com.devtech.school_management_system.dto;

import java.time.LocalDate;

public class AssessmentUpdateDTO {
    private String title;
    private LocalDate date;
    private Double score;
    private Double maxScore;
    private String term;
    private String academicYear;

    public AssessmentUpdateDTO() {
    }

    public AssessmentUpdateDTO(String title, LocalDate date, Double score, Double maxScore, String term, String academicYear) {
        this.title = title;
        this.date = date;
        this.score = score;
        this.maxScore = maxScore;
        this.term = term;
        this.academicYear = academicYear;
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
