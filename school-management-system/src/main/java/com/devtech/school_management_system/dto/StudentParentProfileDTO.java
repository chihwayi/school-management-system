package com.devtech.school_management_system.dto;

import java.util.List;

public class StudentParentProfileDTO {
    private Long id;
    private String name;
    private String mobileNumber;
    private String userType;
    private List<ChildInfoDTO> children; // For parents
    private StudentInfoDTO studentInfo; // For students
    
    public StudentParentProfileDTO() {}
    
    public StudentParentProfileDTO(Long id, String name, String mobileNumber, String userType) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.userType = userType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMobileNumber() {
        return mobileNumber;
    }
    
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public List<ChildInfoDTO> getChildren() {
        return children;
    }
    
    public void setChildren(List<ChildInfoDTO> children) {
        this.children = children;
    }
    
    public StudentInfoDTO getStudentInfo() {
        return studentInfo;
    }
    
    public void setStudentInfo(StudentInfoDTO studentInfo) {
        this.studentInfo = studentInfo;
    }
    
    // Inner DTOs
    public static class ChildInfoDTO {
        private Long id;
        private String name;
        private String studentId;
        private String form;
        private String section;
        private String level;
        private double balance;
        private boolean feesPaid;
        private List<ReportDTO> reports;
        
        public ChildInfoDTO() {}
        
        public ChildInfoDTO(Long id, String name, String studentId, String form, String section, String level, double balance, boolean feesPaid) {
            this.id = id;
            this.name = name;
            this.studentId = studentId;
            this.form = form;
            this.section = section;
            this.level = level;
            this.balance = balance;
            this.feesPaid = feesPaid;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public String getForm() { return form; }
        public void setForm(String form) { this.form = form; }
        
        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
        
        public boolean isFeesPaid() { return feesPaid; }
        public void setFeesPaid(boolean feesPaid) { this.feesPaid = feesPaid; }
        
        public List<ReportDTO> getReports() { return reports; }
        public void setReports(List<ReportDTO> reports) { this.reports = reports; }
    }
    
    public static class StudentInfoDTO {
        private Long id;
        private String name;
        private String studentId;
        private String form;
        private String section;
        private String level;
        private String whatsappNumber;
        private boolean feesPaid;
        private double balance;
        
        public StudentInfoDTO() {}
        
        public StudentInfoDTO(Long id, String name, String studentId, String form, String section, String level, String whatsappNumber, boolean feesPaid, double balance) {
            this.id = id;
            this.name = name;
            this.studentId = studentId;
            this.form = form;
            this.section = section;
            this.level = level;
            this.whatsappNumber = whatsappNumber;
            this.feesPaid = feesPaid;
            this.balance = balance;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public String getForm() { return form; }
        public void setForm(String form) { this.form = form; }
        
        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getWhatsappNumber() { return whatsappNumber; }
        public void setWhatsappNumber(String whatsappNumber) { this.whatsappNumber = whatsappNumber; }
        
        public boolean isFeesPaid() { return feesPaid; }
        public void setFeesPaid(boolean feesPaid) { this.feesPaid = feesPaid; }
        
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
    }
    
    public static class ReportDTO {
        private Long id;
        private String term;
        private String academicYear;
        private String overallGrade;
        private boolean isPublished;
        private boolean canAccess;
        
        public ReportDTO() {}
        
        public ReportDTO(Long id, String term, String academicYear, String overallGrade, boolean isPublished, boolean canAccess) {
            this.id = id;
            this.term = term;
            this.academicYear = academicYear;
            this.overallGrade = overallGrade;
            this.isPublished = isPublished;
            this.canAccess = canAccess;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }
        
        public String getAcademicYear() { return academicYear; }
        public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
        
        public String getOverallGrade() { return overallGrade; }
        public void setOverallGrade(String overallGrade) { this.overallGrade = overallGrade; }
        
        public boolean isPublished() { return isPublished; }
        public void setPublished(boolean published) { isPublished = published; }
        
        public boolean isCanAccess() { return canAccess; }
        public void setCanAccess(boolean canAccess) { this.canAccess = canAccess; }
    }
}
