package com.devtech.school_management_system.dto;

public class StudentUpdateDTO {
    private String firstName;
    private String lastName;
    private String form;
    private String section;
    private String level;

    public StudentUpdateDTO() {
    }

    public StudentUpdateDTO(String firstName, String lastName, String form, String section, String level) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.form = form;
        this.section = section;
        this.level = level;
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
}
