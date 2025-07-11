package com.devtech.school_management_system.dto;

public class SubjectCommentDTO {
    private Long subjectId;
    private String comment;

    public SubjectCommentDTO() {
    }

    public SubjectCommentDTO(Long subjectId, String comment) {
        this.subjectId = subjectId;
        this.comment = comment;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
