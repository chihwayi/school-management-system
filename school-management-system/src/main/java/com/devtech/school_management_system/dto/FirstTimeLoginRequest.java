package com.devtech.school_management_system.dto;

public class FirstTimeLoginRequest {
    private String mobileNumber;
    private String password;
    private String userType; // "STUDENT" or "PARENT"
    
    public FirstTimeLoginRequest() {}
    
    public FirstTimeLoginRequest(String mobileNumber, String password, String userType) {
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.userType = userType;
    }
    
    public String getMobileNumber() {
        return mobileNumber;
    }
    
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
}
