package com.devtech.school_management_system.dto;

public class PaymentStatusDTO {
    private String paymentStatus;

    public PaymentStatusDTO() {
    }

    public PaymentStatusDTO(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}