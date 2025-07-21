package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.*;
import com.devtech.school_management_system.entity.FeePayment;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.enums.PaymentStatus;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.FeePaymentRepository;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeePaymentService {
    
    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;

    public FeePaymentService(FeePaymentRepository feePaymentRepository, StudentRepository studentRepository) {
        this.feePaymentRepository = feePaymentRepository;
        this.studentRepository = studentRepository;
    }

    public PaymentReceiptDTO recordPayment(FeePaymentDTO paymentDTO) {
        Student student = studentRepository.findById(paymentDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Optional<FeePayment> existingPayment = feePaymentRepository
                .findByStudentIdAndTermAndMonthAndAcademicYear(
                        paymentDTO.getStudentId(),
                        paymentDTO.getTerm(),
                        paymentDTO.getMonth(),
                        paymentDTO.getAcademicYear()
                );

        FeePayment payment;
        if (existingPayment.isPresent()) {
            payment = existingPayment.get();
            payment.setAmountPaid(payment.getAmountPaid().add(paymentDTO.getAmountPaid()));
            
            // If they've now paid the full amount or more, update status to FULL_PAYMENT
            if (payment.getAmountPaid().compareTo(payment.getMonthlyFeeAmount()) >= 0) {
                payment.setPaymentStatus(PaymentStatus.FULL_PAYMENT);
            }
        } else {
            payment = new FeePayment();
            payment.setStudent(student);
            payment.setTerm(paymentDTO.getTerm());
            payment.setMonth(paymentDTO.getMonth());
            payment.setAcademicYear(paymentDTO.getAcademicYear());
            payment.setMonthlyFeeAmount(paymentDTO.getMonthlyFeeAmount());
            payment.setAmountPaid(paymentDTO.getAmountPaid());
            payment.setPaymentDate(paymentDTO.getPaymentDate());
        }

        BigDecimal balance = payment.getMonthlyFeeAmount().subtract(payment.getAmountPaid());
        
        // Handle overpayment - if they paid more than required
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            // Set balance to zero for this payment
            payment.setBalance(BigDecimal.ZERO);
            payment.setPaymentStatus(PaymentStatus.FULL_PAYMENT);
            
            // Store the overpaid amount for future term/month
            BigDecimal overpaidAmount = payment.getAmountPaid().subtract(payment.getMonthlyFeeAmount());
            
            // TODO: Create a credit record for the student that can be applied to future payments
            // For now, just log the overpayment
            System.out.println("Student " + student.getFullName() + " has overpaid by " + overpaidAmount + 
                              ". This amount will be credited to their next payment.");
        } else {
            payment.setBalance(balance);
            
            if (balance.compareTo(BigDecimal.ZERO) == 0) {
                payment.setPaymentStatus(PaymentStatus.FULL_PAYMENT);
            } else if (payment.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
                payment.setPaymentStatus(PaymentStatus.PART_PAYMENT);
            } else {
                payment.setPaymentStatus(PaymentStatus.NON_PAYER);
            }
        }

        feePaymentRepository.save(payment);

        return new PaymentReceiptDTO(
                student.getFullName(),
                student.getClassName(),
                payment.getTerm(),
                payment.getMonth(),
                paymentDTO.getAmountPaid(),
                payment.getBalance(),
                payment.getPaymentDate(),
                payment.getMonthlyFeeAmount(),
                payment.getPaymentStatus().toString()
        );
    }

    public List<PaymentStatusSummaryDTO> getPaymentStatusByClass(String form, String section) {
        return List.of(PaymentStatus.values()).stream()
                .map(status -> {
                    List<FeePayment> payments = feePaymentRepository.findByClassAndPaymentStatus(form, section, status);
                    List<StudentPaymentInfoDTO> students = payments.stream()
                            .map(p -> new StudentPaymentInfoDTO(
                                    p.getStudent().getId(),
                                    p.getStudent().getFullName(),
                                    p.getStudent().getClassName(),
                                    p.getAmountPaid(),
                                    p.getBalance(),
                                    p.getPaymentStatus()
                            ))
                            .collect(Collectors.toList());
                    return new PaymentStatusSummaryDTO(form + " " + section, status, students);
                })
                .collect(Collectors.toList());
    }

    public DailyPaymentSummaryDTO getDailyPaymentSummary(LocalDate date) {
        BigDecimal totalAmount = feePaymentRepository.findTotalAmountByDate(date);
        Long totalTransactions = feePaymentRepository.findTotalTransactionsByDate(date);
        
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;
        if (totalTransactions == null) totalTransactions = 0L;
        
        return new DailyPaymentSummaryDTO(date, totalAmount, totalTransactions);
    }

    public List<FeePayment> getStudentPayments(Long studentId, String term, String academicYear) {
        return feePaymentRepository.findByStudentIdAndTermAndAcademicYear(studentId, term, academicYear);
    }

    public List<FeePayment> getPaymentsByDate(LocalDate date) {
        return feePaymentRepository.findByPaymentDate(date);
    }
    
    public List<Student> searchStudentsByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        
        String searchQuery = query.toLowerCase();
        return studentRepository.findAll().stream()
                .filter(student -> 
                    student.getFirstName().toLowerCase().contains(searchQuery) || 
                    student.getLastName().toLowerCase().contains(searchQuery) ||
                    student.getStudentId().toLowerCase().contains(searchQuery))
                .limit(10)  // Limit to 10 results
                .collect(Collectors.toList());
    }
}