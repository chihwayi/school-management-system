package com.devtech.school_management_system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "school_settings")
public class SchoolSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(name = "school_address", columnDefinition = "TEXT")
    private String schoolAddress;

    @Column(name = "school_phone")
    private String schoolPhone;

    @Column(name = "school_email")
    private String schoolEmail;

    @Column(name = "school_logo_url")
    private String schoolLogoUrl;

    @Column(name = "ministry_logo_url")
    private String ministryLogoUrl;

    @Column(name = "principal_name")
    private String principalName;

    @Column(name = "principal_signature_url")
    private String principalSignatureUrl;

    @Column(name = "report_header_text", columnDefinition = "TEXT")
    private String reportHeaderText;

    @Column(name = "report_footer_text", columnDefinition = "TEXT")
    private String reportFooterText;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public SchoolSettings() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getSchoolPhone() {
        return schoolPhone;
    }

    public void setSchoolPhone(String schoolPhone) {
        this.schoolPhone = schoolPhone;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public void setSchoolEmail(String schoolEmail) {
        this.schoolEmail = schoolEmail;
    }

    public String getSchoolLogoUrl() {
        return schoolLogoUrl;
    }

    public void setSchoolLogoUrl(String schoolLogoUrl) {
        this.schoolLogoUrl = schoolLogoUrl;
    }

    public String getMinistryLogoUrl() {
        return ministryLogoUrl;
    }

    public void setMinistryLogoUrl(String ministryLogoUrl) {
        this.ministryLogoUrl = ministryLogoUrl;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getPrincipalSignatureUrl() {
        return principalSignatureUrl;
    }

    public void setPrincipalSignatureUrl(String principalSignatureUrl) {
        this.principalSignatureUrl = principalSignatureUrl;
    }

    public String getReportHeaderText() {
        return reportHeaderText;
    }

    public void setReportHeaderText(String reportHeaderText) {
        this.reportHeaderText = reportHeaderText;
    }

    public String getReportFooterText() {
        return reportFooterText;
    }

    public void setReportFooterText(String reportFooterText) {
        this.reportFooterText = reportFooterText;
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
}