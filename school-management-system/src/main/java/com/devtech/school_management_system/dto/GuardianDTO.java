package com.devtech.school_management_system.dto;

public class GuardianDTO {
    private Long id;
    private String name;
    private String relationship;
    private String phoneNumber;
    private String whatsappNumber;
    private boolean primaryGuardian;

    public GuardianDTO() {
    }

    public GuardianDTO(Long id, String name, String relationship, String phoneNumber, String whatsappNumber, boolean primaryGuardian) {
        this.id = id;
        this.name = name;
        this.relationship = relationship;
        this.phoneNumber = phoneNumber;
        this.whatsappNumber = whatsappNumber;
        this.primaryGuardian = primaryGuardian;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public boolean isPrimaryGuardian() {
        return primaryGuardian;
    }

    public void setPrimaryGuardian(boolean primaryGuardian) {
        this.primaryGuardian = primaryGuardian;
    }
}
