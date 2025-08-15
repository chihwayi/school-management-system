package com.devtech.school_management_system.dto;

import java.util.List;

public class StudentSubjectAssignmentDTO {
    private List<Long> studentIds;
    private List<Long> subjectIds;
    private String form;
    private String section;
    private String academicYear;
    private AssignmentType assignmentType;

    public enum AssignmentType {
        SINGLE,     // Single student assignment
        BULK_CLASS, // All students in a class
        BULK_CUSTOM // Custom list of students
    }

    // Constructors
    public StudentSubjectAssignmentDTO() {}

    // Getters and Setters
    public List<Long> getStudentIds() { return studentIds; }
    public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }

    public List<Long> getSubjectIds() { return subjectIds; }
    public void setSubjectIds(List<Long> subjectIds) { this.subjectIds = subjectIds; }

    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public AssignmentType getAssignmentType() { return assignmentType; }
    public void setAssignmentType(AssignmentType assignmentType) { this.assignmentType = assignmentType; }
}