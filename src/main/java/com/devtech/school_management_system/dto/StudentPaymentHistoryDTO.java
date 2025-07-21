package com.devtech.school_management_system.dto;

import com.devtech.school_management_system.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StudentPaymentHistoryDTO {
    private Long studentId;
    private String studentName;
    private String className;
    private List<PaymentRecord> payments;
    private BigDecimal totalPaid;
    private BigDecimal totalBalance;

    public StudentPaymentHistoryDTO() {
    }

    public StudentPaymentHistoryDTO(Long studentId, String studentName, String className, BigDecimal totalPaid, List<PaymentRecord> payments, BigDecimal totalBalance) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = className;
        this.totalPaid = totalPaid;
        this.payments = payments;
        this.totalBalance = totalBalance;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<PaymentRecord> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentRecord> payments) {
        this.payments = payments;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public static class PaymentRecord {
        private String term;
        private String month;
        private String academicYear;
        private BigDecimal amountPaid;
        private BigDecimal balance;
        private LocalDate paymentDate;
        private PaymentStatus paymentStatus;

        public PaymentRecord() {
        }

        public PaymentRecord(String term, String month, String academicYear, BigDecimal amountPaid, BigDecimal balance, LocalDate paymentDate, PaymentStatus paymentStatus) {
            this.term = term;
            this.month = month;
            this.academicYear = academicYear;
            this.amountPaid = amountPaid;
            this.balance = balance;
            this.paymentDate = paymentDate;
            this.paymentStatus = paymentStatus;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getAcademicYear() {
            return academicYear;
        }

        public void setAcademicYear(String academicYear) {
            this.academicYear = academicYear;
        }

        public BigDecimal getAmountPaid() {
            return amountPaid;
        }

        public void setAmountPaid(BigDecimal amountPaid) {
            this.amountPaid = amountPaid;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public LocalDate getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(LocalDate paymentDate) {
            this.paymentDate = paymentDate;
        }

        public PaymentStatus getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
    }
}