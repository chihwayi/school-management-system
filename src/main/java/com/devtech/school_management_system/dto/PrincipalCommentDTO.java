package com.devtech.school_management_system.dto;

public class PrincipalCommentDTO {
    private String comment;

    public PrincipalCommentDTO() {
    }

    public PrincipalCommentDTO(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}