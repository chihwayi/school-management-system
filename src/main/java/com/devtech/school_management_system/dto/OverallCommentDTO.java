package com.devtech.school_management_system.dto;

public class OverallCommentDTO {
    private Long reportId;
    private String comment;

    public OverallCommentDTO() {}

    public OverallCommentDTO(Long reportId, String comment) {
        this.reportId = reportId;
        this.comment = comment;
    }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}