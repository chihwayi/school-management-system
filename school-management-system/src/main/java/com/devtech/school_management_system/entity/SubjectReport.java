package com.devtech.school_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subject_reports",
        uniqueConstraints = @UniqueConstraint(columnNames = {"report_id", "subject_id"}))
public class SubjectReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "coursework_mark")
    private Double courseworkMark;

    @Column(name = "exam_mark")
    private Double examMark;

    @Column(name = "total_mark")
    private Double totalMark;

    @Column(name = "grade")
    private String grade;

    @Column(name = "teacher_comment", columnDefinition = "TEXT")
    private String teacherComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    
    @Column(name = "teacher_signature_url")
    private String teacherSignatureUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public SubjectReport() {
    }

    public SubjectReport(Long id, Report report, Subject subject, Double courseworkMark, Double examMark, Double totalMark, String grade, String teacherComment, Teacher teacher, String teacherSignatureUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.report = report;
        this.subject = subject;
        this.courseworkMark = courseworkMark;
        this.examMark = examMark;
        this.totalMark = totalMark;
        this.grade = grade;
        this.teacherComment = teacherComment;
        this.teacher = teacher;
        this.teacherSignatureUrl = teacherSignatureUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Double getCourseworkMark() {
        return courseworkMark;
    }

    public void setCourseworkMark(Double courseworkMark) {
        this.courseworkMark = courseworkMark;
    }

    public Double getExamMark() {
        return examMark;
    }

    public void setExamMark(Double examMark) {
        this.examMark = examMark;
    }

    public Double getTotalMark() {
        return totalMark;
    }

    public void setTotalMark(Double totalMark) {
        this.totalMark = totalMark;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
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
    
    public String getTeacherSignatureUrl() {
        return teacherSignatureUrl;
    }

    public void setTeacherSignatureUrl(String teacherSignatureUrl) {
        this.teacherSignatureUrl = teacherSignatureUrl;
    }
}
