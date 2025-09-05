package com.devtech.school_management_system.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String term;

    @Column(nullable = false)
    private String academicYear;

    @Column(name = "overall_comment", columnDefinition = "TEXT")
    private String overallComment;
    
    @Column(name = "principal_comment", columnDefinition = "TEXT")
    private String principalComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_id")
    private Teacher classTeacher;
    
    @Column(name = "attendance_days")
    private Integer attendanceDays;
    
    @Column(name = "total_school_days")
    private Integer totalSchoolDays;
    
    @Column(name = "class_teacher_signature_url")
    private String classTeacherSignatureUrl;
    
    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "is_finalized", nullable = false)
    private boolean finalized = false;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubjectReport> subjectReports = new ArrayList<>();

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

    public Report() {
    }

    public Report(Long id, Student student, String term, String academicYear, String overallComment, String principalComment, 
                Teacher classTeacher, boolean finalized, List<SubjectReport> subjectReports, Integer attendanceDays, 
                Integer totalSchoolDays, String classTeacherSignatureUrl, String paymentStatus, 
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.student = student;
        this.term = term;
        this.academicYear = academicYear;
        this.overallComment = overallComment;
        this.principalComment = principalComment;
        this.classTeacher = classTeacher;
        this.finalized = finalized;
        this.subjectReports = subjectReports;
        this.attendanceDays = attendanceDays;
        this.totalSchoolDays = totalSchoolDays;
        this.classTeacherSignatureUrl = classTeacherSignatureUrl;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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

    public String getOverallComment() {
        return overallComment;
    }

    public void setOverallComment(String overallComment) {
        this.overallComment = overallComment;
    }

    public Teacher getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(Teacher classTeacher) {
        this.classTeacher = classTeacher;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    public List<SubjectReport> getSubjectReports() {
        if (subjectReports == null) {
            subjectReports = new ArrayList<>();
        }
        return subjectReports;
    }

    public void setSubjectReports(List<SubjectReport> subjectReports) {
        this.subjectReports = subjectReports;
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
    
    public String getPrincipalComment() {
        return principalComment;
    }

    public void setPrincipalComment(String principalComment) {
        this.principalComment = principalComment;
    }

    public Integer getAttendanceDays() {
        return attendanceDays;
    }

    public void setAttendanceDays(Integer attendanceDays) {
        this.attendanceDays = attendanceDays;
    }

    public Integer getTotalSchoolDays() {
        return totalSchoolDays;
    }

    public void setTotalSchoolDays(Integer totalSchoolDays) {
        this.totalSchoolDays = totalSchoolDays;
    }

    public String getClassTeacherSignatureUrl() {
        return classTeacherSignatureUrl;
    }

    public void setClassTeacherSignatureUrl(String classTeacherSignatureUrl) {
        this.classTeacherSignatureUrl = classTeacherSignatureUrl;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}

