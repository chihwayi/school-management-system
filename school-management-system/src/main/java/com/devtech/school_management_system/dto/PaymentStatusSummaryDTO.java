package com.devtech.school_management_system.dto;

import com.devtech.school_management_system.enums.PaymentStatus;

import java.util.List;

public class PaymentStatusSummaryDTO {
    private String className;
    private PaymentStatus status;
    private List<StudentPaymentInfoDTO> students;

    // Constructors
    public PaymentStatusSummaryDTO() {}

    public PaymentStatusSummaryDTO(String className, PaymentStatus status, List<StudentPaymentInfoDTO> students) {
        this.className = className;
        this.status = status;
        this.students = students;
    }

    // Getters and Setters
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public List<StudentPaymentInfoDTO> getStudents() { return students; }
    public void setStudents(List<StudentPaymentInfoDTO> students) { this.students = students; }
}