package com.devtech.school_management_system.dto;

public class SchoolConfigDTO {
    private String name;
    private String description;
    private String primaryColor;
    private String secondaryColor;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String website;

    public SchoolConfigDTO() {
    }

    public SchoolConfigDTO(String name, String description, String primaryColor, String secondaryColor, String contactEmail, String contactPhone, String address, String website) {
        this.name = name;
        this.description = description;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.address = address;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
