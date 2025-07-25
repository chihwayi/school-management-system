package com.devtech.school_management_system.dto;

import java.util.List;

public class TeacherSubjectAssignmentDTO {
    private Long teacherId;
    private String firstName;
    private String lastName;
    private String employeeId;
    private List<String> classes;

    public TeacherSubjectAssignmentDTO() {}

    public TeacherSubjectAssignmentDTO(Long teacherId, String firstName, String lastName, String employeeId, List<String> classes) {
        this.teacherId = teacherId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeId = employeeId;
        this.classes = classes;
    }

    // Getters and Setters
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public List<String> getClasses() { return classes; }
    public void setClasses(List<String> classes) { this.classes = classes; }
}