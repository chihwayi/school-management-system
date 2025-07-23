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
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            // Set balance to the actual value (zero or negative)
            payment.setBalance(balance);
            payment.setPaymentStatus(PaymentStatus.FULL_PAYMENT);
            
            // If there's an overpayment, log it
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal overpaidAmount = payment.getAmountPaid().subtract(payment.getMonthlyFeeAmount());
                System.out.println("Student " + student.getFullName() + " has overpaid by " + overpaidAmount + 
                                  ". This amount will be credited to their next payment.");
            }
        } else {
            payment.setBalance(balance);
            
            if (payment.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
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
    
    /**
     * Fix payment status for records that have a zero or negative balance but still show PART_PAYMENT status
     */
    @Transactional
    public void fixPaymentStatusForCompletedPayments() {
        List<FeePayment> payments = feePaymentRepository.findAll();
        int updatedCount = 0;
        
        for (FeePayment payment : payments) {
            if (payment.getPaymentStatus() == PaymentStatus.PART_PAYMENT && 
                payment.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
                payment.setPaymentStatus(PaymentStatus.FULL_PAYMENT);
                feePaymentRepository.save(payment);
                updatedCount++;
            }
        }
        
        System.out.println("Fixed payment status for " + updatedCount + " records");
    }
    
    /**
     * Fix payment issues for a specific student by name
     */
    @Transactional
    public String fixStudentPaymentByName(String studentName) {
        List<Student> students = studentRepository.findAll().stream()
            .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase()
                .contains(studentName.toLowerCase()))
            .collect(Collectors.toList());
        
        if (students.isEmpty()) {
            return "No students found matching name: " + studentName;
        }
        
        StringBuilder result = new StringBuilder();
        int fixedCount = 0;
        
        for (Student student : students) {
            result.append("Processing student: ").append(student.getFirstName())
                  .append(" ").append(student.getLastName()).append("\n");
            
            List<FeePayment> payments = feePaymentRepository.findByStudentId(student.getId());
            if (payments == null || payments.isEmpty()) {
                result.append("No payments found for this student\n");
                continue;
            }
            
            for (FeePayment payment : payments) {
                result.append("Payment ID: ").append(payment.getId())
                      .append(", Status: ").append(payment.getPaymentStatus())
                      .append(", Balance: ").append(payment.getBalance()).append("\n");
                
                // Fix any payment with balance <= 0 but not marked as FULL_PAYMENT
                if (payment.getBalance().compareTo(BigDecimal.ZERO) <= 0 && 
                    payment.getPaymentStatus() != PaymentStatus.FULL_PAYMENT) {
                    payment.setPaymentStatus(PaymentStatus.FULL_PAYMENT);
                    feePaymentRepository.save(payment);
                    result.append("Fixed: Changed status to FULL_PAYMENT\n");
                    fixedCount++;
                }
                // Fix any payment with balance > 0 but marked as FULL_PAYMENT
                else if (payment.getBalance().compareTo(BigDecimal.ZERO) > 0 && 
                         payment.getPaymentStatus() == PaymentStatus.FULL_PAYMENT) {
                    payment.setPaymentStatus(PaymentStatus.PART_PAYMENT);
                    feePaymentRepository.save(payment);
                    result.append("Fixed: Changed status to PART_PAYMENT\n");
                    fixedCount++;
                }
            }
        }
        
        result.append("Fixed ").append(fixedCount).append(" payment records");
        return result.toString();
    }
}