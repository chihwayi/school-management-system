package com.devtech.school_management_system.dto;

import java.util.List;

public class StudentReportDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String form;
    private String section;
    private String term;
    private String academicYear;
    private List<SubjectReportDTO> subjectReports;
    private String overallComment;
    private boolean finalized;
    private String classTeacherSignatureUrl;

    public StudentReportDTO() {}

    public StudentReportDTO(Long id, Long studentId, String studentName, String form, String section,
                           String term, String academicYear, List<SubjectReportDTO> subjectReports,
                           String overallComment, boolean finalized) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.form = form;
        this.section = section;
        this.term = term;
        this.academicYear = academicYear;
        this.subjectReports = subjectReports;
        this.overallComment = overallComment;
        this.finalized = finalized;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public List<SubjectReportDTO> getSubjectReports() { return subjectReports; }
    public void setSubjectReports(List<SubjectReportDTO> subjectReports) { this.subjectReports = subjectReports; }

    public String getOverallComment() { return overallComment; }
    public void setOverallComment(String overallComment) { this.overallComment = overallComment; }

    public boolean isFinalized() { return finalized; }
    public void setFinalized(boolean finalized) { this.finalized = finalized; }

    public String getClassTeacherSignatureUrl() { return classTeacherSignatureUrl; }
    public void setClassTeacherSignatureUrl(String classTeacherSignatureUrl) { this.classTeacherSignatureUrl = classTeacherSignatureUrl; }
}