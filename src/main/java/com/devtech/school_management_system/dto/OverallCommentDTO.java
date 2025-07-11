package com.devtech.school_management_system.dto;

public class OverallCommentDTO {
    private String comment;

    public OverallCommentDTO() {
    }

    public OverallCommentDTO(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
