package com.devtech.school_management_system.dto;

public class PasswordResetDTO {
    private String username;
    private String newPassword;
    
    public PasswordResetDTO() {}
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}