package com.devtech.school_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditLogDTO {
    private Long id;
    private String action;
    private String description;
    private String performedBy;
    private LocalDateTime timestamp;
    private Long paymentId;
    private Long studentId;
    private BigDecimal amount;

    public AuditLogDTO() {
    }

    public AuditLogDTO(Long id, String action, String description, String performedBy, LocalDateTime timestamp, Long paymentId, Long studentId, BigDecimal amount) {
        this.id = id;
        this.action = action;
        this.description = description;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.paymentId = paymentId;
        this.studentId = studentId;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}