package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.StudentParentAuth;
import com.devtech.school_management_system.entity.FeePayment;
import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.StudentParentAuthRepository;
import com.devtech.school_management_system.repository.FeePaymentRepository;
import com.devtech.school_management_system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentDashboardService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private StudentParentAuthRepository authRepository;
    
    @Autowired
    private FeePaymentRepository feePaymentRepository;
    
    @Autowired
    private StudentService studentService;
    
    public Map<String, Object> getStudentProfile(String mobileNumber) {
        // Find the student by mobile number
        Optional<Student> studentOpt = studentRepository.findByWhatsappNumber(mobileNumber);
        
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", student.getId());
            profile.put("name", student.getFirstName() + " " + student.getLastName());
            profile.put("studentId", student.getStudentId());
            profile.put("form", student.getForm());
            profile.put("section", student.getSection());
            profile.put("level", student.getLevel());
            profile.put("phoneNumber", student.getWhatsappNumber());
            profile.put("isActive", true); // Students are considered active by default
            // Calculate actual balance from fee payments
            List<FeePayment> payments = feePaymentRepository.findByStudentId(student.getId());
            double totalBalance = payments.stream()
                .mapToDouble(payment -> payment.getBalance().doubleValue())
                .sum();
            profile.put("balance", totalBalance);
            profile.put("gender", student.getGender());
            profile.put("dateOfBirth", student.getDateOfBirth());
            profile.put("enrollmentDate", student.getEnrollmentDate());
            
            return profile;
        }
        
        throw new RuntimeException("Student not found with mobile number: " + mobileNumber);
    }
    
    public List<Map<String, Object>> getStudentAssignments(String mobileNumber) {
        // TODO: Implement actual assignment logic based on student's subjects and classes
        // For now, return mock data
        return List.of(
            Map.of(
                "id", 1L,
                "title", "Mathematics Assignment 1",
                "subject", "Mathematics",
                "dueDate", "2024-09-15",
                "status", "pending",
                "description", "Complete exercises 1-10 from Chapter 3"
            ),
            Map.of(
                "id", 2L,
                "title", "English Essay",
                "subject", "English",
                "dueDate", "2024-09-10",
                "status", "overdue",
                "description", "Write a 500-word essay on Shakespeare"
            ),
            Map.of(
                "id", 3L,
                "title", "Science Lab Report",
                "subject", "Science",
                "dueDate", "2024-09-20",
                "status", "pending",
                "description", "Complete lab report for Chemistry experiment"
            )
        );
    }
    
    public List<Map<String, Object>> getStudentReports(String mobileNumber) {
        // TODO: Implement actual report logic based on student's academic records
        // For now, return mock data
        return List.of(
            Map.of(
                "id", 1L,
                "title", "Term 1 Report",
                "term", "Term 1",
                "academicYear", "2024",
                "overallGrade", "A",
                "isPublished", true,
                "canAccess", true
            ),
            Map.of(
                "id", 2L,
                "title", "Term 2 Report",
                "term", "Term 2",
                "academicYear", "2024",
                "overallGrade", "B+",
                "isPublished", true,
                "canAccess", true
            ),
            Map.of(
                "id", 3L,
                "title", "Term 3 Report",
                "term", "Term 3",
                "academicYear", "2024",
                "overallGrade", "A-",
                "isPublished", false,
                "canAccess", false
            )
        );
    }
    
    public Map<String, Object> getStudentFinance(String mobileNumber) {
        // Find the student by mobile number
        Optional<Student> studentOpt = studentRepository.findByWhatsappNumber(mobileNumber);
        
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            
            // Get all fee payments for this student
            List<FeePayment> payments = feePaymentRepository.findByStudentId(student.getId());
            
            // Calculate totals
            double totalPaid = payments.stream()
                .mapToDouble(payment -> payment.getAmountPaid().doubleValue())
                .sum();
            
            double totalBalance = payments.stream()
                .mapToDouble(payment -> payment.getBalance().doubleValue())
                .sum();
            
            // Create payment history
            List<Map<String, Object>> paymentHistory = payments.stream()
                .map(payment -> {
                    Map<String, Object> paymentMap = new HashMap<>();
                    paymentMap.put("id", payment.getId());
                    paymentMap.put("term", payment.getTerm());
                    paymentMap.put("month", payment.getMonth());
                    paymentMap.put("academicYear", payment.getAcademicYear());
                    paymentMap.put("monthlyFeeAmount", payment.getMonthlyFeeAmount().doubleValue());
                    paymentMap.put("amountPaid", payment.getAmountPaid().doubleValue());
                    paymentMap.put("balance", payment.getBalance().doubleValue());
                    paymentMap.put("paymentStatus", payment.getPaymentStatus().toString());
                    paymentMap.put("paymentDate", payment.getPaymentDate().toString());
                    return paymentMap;
                })
                .toList();
            
            Map<String, Object> finance = new HashMap<>();
            finance.put("studentId", student.getId());
            finance.put("studentName", student.getFirstName() + " " + student.getLastName());
            finance.put("totalPaid", totalPaid);
            finance.put("totalBalance", totalBalance);
            finance.put("isFeesPaid", totalBalance <= 0);
            finance.put("paymentHistory", paymentHistory);
            
            return finance;
        }
        
        throw new RuntimeException("Student not found with mobile number: " + mobileNumber);
    }
    
    public List<Map<String, Object>> getStudentSubjects(String mobileNumber) {
        Optional<Student> studentOpt = studentRepository.findByWhatsappNumber(mobileNumber);
        
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            
            // For now, return mock data based on the student ID to avoid lazy loading issues
            // TODO: Implement proper subject fetching when lazy loading is resolved
            List<Map<String, Object>> subjects = new ArrayList<>();
            
            if (student.getId() == 1L) {
                // Skylar's subjects
                subjects.add(Map.of(
                    "id", 2L,
                    "name", "Literature in English",
                    "code", "LIT",
                    "description", "English Literature for ZJC",
                    "level", "JUNIOR_SECONDARY"
                ));
                subjects.add(Map.of(
                    "id", 3L,
                    "name", "English",
                    "code", "ENG",
                    "description", "English Language for Juniors",
                    "level", "JUNIOR_SECONDARY"
                ));
                subjects.add(Map.of(
                    "id", 4L,
                    "name", "Mathematics",
                    "code", "MATH",
                    "description", "Mathematics for Junior Secondary",
                    "level", "JUNIOR_SECONDARY"
                ));
            } else if (student.getId() == 2L) {
                // Kaylee's subjects
                subjects.add(Map.of(
                    "id", 2L,
                    "name", "Literature in English",
                    "code", "LIT",
                    "description", "English Literature for ZJC",
                    "level", "JUNIOR_SECONDARY"
                ));
                subjects.add(Map.of(
                    "id", 3L,
                    "name", "English",
                    "code", "ENG",
                    "description", "English Language for Juniors",
                    "level", "JUNIOR_SECONDARY"
                ));
            }
            
            return subjects;
        }
        
        throw new RuntimeException("Student not found with mobile number: " + mobileNumber);
    }
}
