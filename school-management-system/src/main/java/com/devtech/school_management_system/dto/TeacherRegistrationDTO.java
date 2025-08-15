package com.devtech.school_management_system.dto;

public class TeacherRegistrationDTO {
    private String firstName;
    private String lastName;
    private String employeeId;
    private String username;
    private String email;
    private String password;

    public TeacherRegistrationDTO() {
    }

    public TeacherRegistrationDTO(String firstName, String lastName, String employeeId, String username, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeId = employeeId;
        this.username = username;
        this.email = email;
        this.password = password;
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
