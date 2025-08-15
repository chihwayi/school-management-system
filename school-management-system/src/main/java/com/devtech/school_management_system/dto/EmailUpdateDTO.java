package com.devtech.school_management_system.dto;

public class EmailUpdateDTO {
    private String username;
    private String newEmail;
    
    public EmailUpdateDTO() {}
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNewEmail() {
        return newEmail;
    }
    
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}