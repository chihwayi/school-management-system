package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.StudentParentAuth;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.StudentParentAuthRepository;
import com.devtech.school_management_system.service.StudentDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentDashboardController {
    
    @Autowired
    private StudentDashboardService studentDashboardService;
    
    @Autowired
    private StudentParentAuthRepository authRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @GetMapping("/assignments")
    public ResponseEntity<List<Map<String, Object>>> getStudentAssignments(@RequestParam String mobileNumber) {
        try {
            List<Map<String, Object>> assignments = studentDashboardService.getStudentAssignments(mobileNumber);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/reports")
    public ResponseEntity<List<Map<String, Object>>> getStudentReports(@RequestParam String mobileNumber) {
        try {
            List<Map<String, Object>> reports = studentDashboardService.getStudentReports(mobileNumber);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getStudentProfile(@RequestParam String mobileNumber) {
        try {
            Map<String, Object> profile = studentDashboardService.getStudentProfile(mobileNumber);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/finance")
    public ResponseEntity<Map<String, Object>> getStudentFinance(@RequestParam String mobileNumber) {
        try {
            Map<String, Object> finance = studentDashboardService.getStudentFinance(mobileNumber);
            return ResponseEntity.ok(finance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/subjects")
    public ResponseEntity<List<Map<String, Object>>> getStudentSubjects(@RequestParam String mobileNumber) {
        try {
            List<Map<String, Object>> subjects = studentDashboardService.getStudentSubjects(mobileNumber);
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
