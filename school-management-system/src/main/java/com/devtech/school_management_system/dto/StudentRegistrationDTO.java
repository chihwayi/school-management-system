package com.devtech.school_management_system.dto;

import java.util.List;

public class StudentRegistrationDTO {
    private String firstName;
    private String lastName;
    private String studentId;
    private String form;
    private String section;
    private String level;
    private String academicYear;
    private String enrollmentDate;
    private String whatsappNumber;
    private String dateOfBirth;
    private String gender;
    private List<Long> subjectIds; // Optional: list of subjects to assign
    private List<GuardianDTO> guardians; // Guardian information

    public StudentRegistrationDTO() {
    }

    public StudentRegistrationDTO(String firstName, String lastName, String studentId, String form, String section, String level, List<Long> subjectIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.form = form;
        this.section = section;
        this.level = level;
        this.subjectIds = subjectIds;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public List<Long> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(List<Long> subjectIds) {
        this.subjectIds = subjectIds;
    }

    public List<GuardianDTO> getGuardians() {
        return guardians;
    }

    public void setGuardians(List<GuardianDTO> guardians) {
        this.guardians = guardians;
    }
}
