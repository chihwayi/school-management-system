package com.devtech.school_management_system.dto;

public class SubjectCommentDTO {
    private Long reportId;
    private Long subjectId;
    private String comment;

    public SubjectCommentDTO() {}

    public SubjectCommentDTO(Long reportId, Long subjectId, String comment) {
        this.reportId = reportId;
        this.subjectId = subjectId;
        this.comment = comment;
    }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}