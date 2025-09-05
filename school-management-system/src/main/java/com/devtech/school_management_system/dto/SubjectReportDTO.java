package com.devtech.school_management_system.dto;

public class SubjectReportDTO {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Double courseworkMark;
    private Double examMark;
    private Double finalMark;
    private String comment;
    private Long teacherId;
    private String teacherName;
    private String teacherSignatureUrl;

    public SubjectReportDTO() {}

    public SubjectReportDTO(Long id, Long subjectId, String subjectName, String subjectCode,
                           Double courseworkMark, Double examMark, Double finalMark,
                           String comment, Long teacherId, String teacherName) {
        this.id = id;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.courseworkMark = courseworkMark;
        this.examMark = examMark;
        this.finalMark = finalMark;
        this.comment = comment;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public Double getCourseworkMark() { return courseworkMark; }
    public void setCourseworkMark(Double courseworkMark) { this.courseworkMark = courseworkMark; }

    public Double getExamMark() { return examMark; }
    public void setExamMark(Double examMark) { this.examMark = examMark; }

    public Double getFinalMark() { return finalMark; }
    public void setFinalMark(Double finalMark) { this.finalMark = finalMark; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getTeacherSignatureUrl() { return teacherSignatureUrl; }
    public void setTeacherSignatureUrl(String teacherSignatureUrl) { this.teacherSignatureUrl = teacherSignatureUrl; }
}