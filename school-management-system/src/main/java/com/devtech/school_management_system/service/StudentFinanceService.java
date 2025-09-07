package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.FeePayment;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.repository.FeePaymentRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentFinanceService {
    
    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;

    public StudentFinanceService(FeePaymentRepository feePaymentRepository, 
                                StudentRepository studentRepository) {
        this.feePaymentRepository = feePaymentRepository;
        this.studentRepository = studentRepository;
    }

    public boolean isFeesPaid(Long studentId) {
        List<FeePayment> payments = feePaymentRepository.findByStudentId(studentId);
        if (payments == null || payments.isEmpty()) {
            return false;
        }
        
        // Check if all payments have zero balance (fully paid)
        return payments.stream()
                .allMatch(payment -> payment.getBalance() != null && payment.getBalance().compareTo(BigDecimal.ZERO) <= 0);
    }

    public Map<String, Object> getStudentFinanceStatus(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        List<FeePayment> payments = feePaymentRepository.findByStudentId(studentId);
        if (payments == null) {
            payments = List.of();
        }
        
        BigDecimal totalPaid = payments.stream()
                .map(FeePayment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalBalance = payments.stream()
                .map(FeePayment::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        boolean isFeesPaid = totalBalance.compareTo(BigDecimal.ZERO) <= 0;
        
        List<Map<String, Object>> paymentHistory = payments.stream()
                .map(payment -> {
                    Map<String, Object> record = new HashMap<>();
                    record.put("id", payment.getId());
                    record.put("amount", payment.getAmountPaid());
                    record.put("date", payment.getPaymentDate());
                    record.put("month", payment.getMonth());
                    record.put("term", payment.getTerm());
                    record.put("academicYear", payment.getAcademicYear());
                    record.put("status", payment.getPaymentStatus());
                    return record;
                })
                .toList();
        
        Map<String, Object> status = new HashMap<>();
        status.put("studentId", studentId);
        status.put("studentName", student.getFirstName() + " " + student.getLastName());
        status.put("totalPaid", totalPaid);
        status.put("totalBalance", totalBalance);
        status.put("isFeesPaid", isFeesPaid);
        status.put("paymentHistory", paymentHistory);
        
        return status;
    }
}
