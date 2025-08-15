package com.devtech.school_management_system.dto;

import java.time.LocalDate;

public class AssessmentUpdateDTO {
    private String title;
    private LocalDate date;
    private Double score;
    private Double maxScore;

    public AssessmentUpdateDTO() {
    }

    public AssessmentUpdateDTO(String title, LocalDate date, Double score, Double maxScore) {
        this.title = title;
        this.date = date;
        this.score = score;
        this.maxScore = maxScore;
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
}
