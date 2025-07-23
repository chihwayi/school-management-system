package com.devtech.school_management_system.dto;

public class SignatureDTO {
    private String signatureUrl;

    public SignatureDTO() {
    }

    public SignatureDTO(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }
}