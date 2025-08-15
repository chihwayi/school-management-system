package com.devtech.school_management_system.dto;

import java.util.Set;

public class RoleUpdateDTO {
    private String username;
    private Set<String> roles;
    
    public RoleUpdateDTO() {}
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}