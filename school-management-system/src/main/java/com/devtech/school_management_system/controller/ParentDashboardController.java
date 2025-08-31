package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.repository.GuardianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/parent")
@CrossOrigin(origins = "*")
public class ParentDashboardController {
    
    @Autowired
    private GuardianRepository guardianRepository;
    
        @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getParentProfile(@RequestParam String mobileNumber) {
        try {
            // Find the guardian by mobile number with student data loaded
            List<Guardian> guardians = guardianRepository.findByWhatsappNumberWithStudent(mobileNumber);
            
            if (!guardians.isEmpty()) {
                Guardian guardian = guardians.get(0); // Get the first guardian
                
                Map<String, Object> profile = new HashMap<>();
                profile.put("id", guardian.getId());
                profile.put("name", guardian.getName());
                profile.put("phoneNumber", guardian.getPhoneNumber());
                profile.put("whatsappNumber", guardian.getWhatsappNumber());
                profile.put("relationship", guardian.getRelationship());
                profile.put("isPrimaryGuardian", guardian.isPrimaryGuardian());
                
                // Get student details (student data is now properly loaded)
                profile.put("studentId", guardian.getStudent().getId());
                profile.put("studentName", guardian.getStudent().getFirstName() + " " + guardian.getStudent().getLastName());
                profile.put("studentClass", guardian.getStudent().getForm() + " " + guardian.getStudent().getSection());
                
                return ResponseEntity.ok(profile);
            }
            
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/children")
    public ResponseEntity<List<Map<String, Object>>> getChildren(@RequestParam String mobileNumber) {
        try {
            // Find all guardians by mobile number with student data loaded
            List<Guardian> guardians = guardianRepository.findByWhatsappNumberWithStudent(mobileNumber);
            
            if (!guardians.isEmpty()) {
                // Return all children for this guardian with actual student data
                List<Map<String, Object>> children = guardians.stream()
                    .map(guardian -> {
                        Map<String, Object> child = new HashMap<>();
                        child.put("id", guardian.getStudent().getId());
                        child.put("name", guardian.getStudent().getFirstName() + " " + guardian.getStudent().getLastName());
                        child.put("studentId", guardian.getStudent().getStudentId());
                        child.put("form", guardian.getStudent().getForm());
                        child.put("section", guardian.getStudent().getSection());
                        child.put("level", guardian.getStudent().getLevel());
                        child.put("whatsappNumber", guardian.getStudent().getWhatsappNumber());
                        child.put("relationship", guardian.getRelationship());
                        child.put("isPrimaryGuardian", guardian.isPrimaryGuardian());
                        return child;
                    })
                    .toList();
                
                return ResponseEntity.ok(children);
            }
            
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
