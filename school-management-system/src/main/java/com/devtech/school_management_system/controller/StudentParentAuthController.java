package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.StudentParentLoginRequest;
import com.devtech.school_management_system.dto.FirstTimeLoginRequest;
import com.devtech.school_management_system.dto.StudentParentProfileDTO;
import com.devtech.school_management_system.service.StudentParentAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class StudentParentAuthController {
    
    @Autowired
    private StudentParentAuthService authService;
    
    @PostMapping("/check-first-time")
    public ResponseEntity<Map<String, Object>> checkFirstTimeLogin(@RequestBody Map<String, String> request) {
        try {
            String mobileNumber = request.get("mobileNumber");
            String userType = request.get("userType");
            
            boolean isFirstTime = authService.checkFirstTimeLogin(mobileNumber, userType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isFirstTime", isFirstTime);
            response.put("message", "Check completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/first-time-login")
    public ResponseEntity<Map<String, Object>> firstTimeLogin(@RequestBody FirstTimeLoginRequest request) {
        try {
            String token = authService.firstTimeLogin(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Account created and logged in successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/student-parent-login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody StudentParentLoginRequest request) {
        try {
            String token = authService.login(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/student/profile")
    public ResponseEntity<StudentParentProfileDTO> getStudentProfile(@RequestParam Long studentId) {
        try {
            StudentParentProfileDTO profile = authService.getStudentProfile(studentId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/parent/profile")
    public ResponseEntity<StudentParentProfileDTO> getParentProfile(@RequestParam Long guardianId) {
        try {
            StudentParentProfileDTO profile = authService.getParentProfile(guardianId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
