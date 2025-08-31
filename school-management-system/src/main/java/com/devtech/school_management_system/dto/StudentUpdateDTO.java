package com.devtech.school_management_system.dto;

public class StudentUpdateDTO {
    private String firstName;
    private String lastName;
    private String form;
    private String section;
    private String level;
    private String enrollmentDate;
    private String whatsappNumber;
    private String dateOfBirth;
    private String gender;

    public StudentUpdateDTO() {
    }

    public StudentUpdateDTO(String firstName, String lastName, String form, String section, String level, String whatsappNumber, String dateOfBirth, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.form = form;
        this.section = section;
        this.level = level;
        this.whatsappNumber = whatsappNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
