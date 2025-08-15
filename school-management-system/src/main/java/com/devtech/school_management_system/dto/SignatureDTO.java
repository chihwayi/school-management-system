package com.devtech.school_management_system.dto;

public class SignatureDTO {
    private Long id;
    private String signatureUrl;
    private String teacherName;
    private String role;
    private String uploadedAt;

    public SignatureDTO() {}

    public SignatureDTO(Long id, String signatureUrl, String teacherName, String role, String uploadedAt) {
        this.id = id;
        this.signatureUrl = signatureUrl;
        this.teacherName = teacherName;
        this.role = role;
        this.uploadedAt = uploadedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSignatureUrl() { return signatureUrl; }
    public void setSignatureUrl(String signatureUrl) { this.signatureUrl = signatureUrl; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}