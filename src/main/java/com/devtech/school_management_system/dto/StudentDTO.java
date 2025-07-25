package com.devtech.school_management_system.dto;

public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String studentId;
    private String form;
    private String section;
    private String level;
    private String academicYear;

    public StudentDTO() {}

    public StudentDTO(Long id, String firstName, String lastName, String studentId, 
                     String form, String section, String level, String academicYear) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.form = form;
        this.section = section;
        this.level = level;
        this.academicYear = academicYear;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getClassName() {
        return form + " " + section;
    }
}