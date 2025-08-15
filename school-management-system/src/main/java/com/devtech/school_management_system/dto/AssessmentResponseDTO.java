package com.devtech.school_management_system.dto;

import com.devtech.school_management_system.enums.AssessmentType;
import java.time.LocalDate;

public class AssessmentResponseDTO {
    private Long id;
    private String title;
    private LocalDate date;
    private Double score;
    private Double maxScore;
    private AssessmentType type;
    private String term;
    private String academicYear;
    private Double percentage;
    
    // Student fields
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private String studentForm;
    private String studentSection;
    
    // Subject fields
    private Long subjectId;
    private String subjectName;
    private String subjectCode;

    public AssessmentResponseDTO() {}

    public AssessmentResponseDTO(Long id, String title, LocalDate date, Double score, 
                                Double maxScore, AssessmentType type, String term, 
                                String academicYear, Long studentId, String studentFirstName,
                                String studentLastName, String studentForm, String studentSection,
                                Long subjectId, String subjectName, String subjectCode) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.score = score;
        this.maxScore = maxScore;
        this.type = type;
        this.term = term;
        this.academicYear = academicYear;
        this.percentage = (maxScore != null && maxScore != 0) ? (score / maxScore) * 100 : 0.0;
        this.studentId = studentId;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.studentForm = studentForm;
        this.studentSection = studentSection;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }

    public AssessmentType getType() { return type; }
    public void setType(AssessmentType type) { this.type = type; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    
    // Student getters and setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public String getStudentFirstName() { return studentFirstName; }
    public void setStudentFirstName(String studentFirstName) { this.studentFirstName = studentFirstName; }
    
    public String getStudentLastName() { return studentLastName; }
    public void setStudentLastName(String studentLastName) { this.studentLastName = studentLastName; }
    
    public String getStudentForm() { return studentForm; }
    public void setStudentForm(String studentForm) { this.studentForm = studentForm; }
    
    public String getStudentSection() { return studentSection; }
    public void setStudentSection(String studentSection) { this.studentSection = studentSection; }
    
    // Subject getters and setters
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
}