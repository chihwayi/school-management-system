package com.devtech.admin.controller;

import com.devtech.admin.dto.SchoolDTO;
import com.devtech.admin.dto.SchoolCreateRequest;
import com.devtech.admin.dto.SchoolUpdateRequest;
import com.devtech.admin.dto.UserCreateRequest;
import com.devtech.admin.dto.UserDTO;
import com.devtech.admin.service.SchoolService;
import com.devtech.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/schools")
@CrossOrigin(origins = "*")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping
    public ResponseEntity<List<SchoolDTO>> getAllSchools(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String planType) {
        
        List<SchoolDTO> schools = schoolService.getAllSchools(page, size, status, planType);
        return ResponseEntity.ok(schools);
    }

    @GetMapping("/{schoolId}")
    public ResponseEntity<SchoolDTO> getSchoolById(@PathVariable String schoolId) {
        SchoolDTO school = schoolService.getSchoolById(schoolId);
        return ResponseEntity.ok(school);
    }

    @PostMapping
    public ResponseEntity<SchoolDTO> createSchool(@RequestBody SchoolCreateRequest request) {
        SchoolDTO school = schoolService.createSchool(request);
        return ResponseEntity.ok(school);
    }

    @PutMapping("/{schoolId}")
    public ResponseEntity<SchoolDTO> updateSchool(
            @PathVariable String schoolId,
            @RequestBody SchoolUpdateRequest request) {
        
        SchoolDTO school = schoolService.updateSchool(schoolId, request);
        return ResponseEntity.ok(school);
    }

    @DeleteMapping("/{schoolId}")
    public ResponseEntity<Map<String, String>> deleteSchool(@PathVariable String schoolId) {
        schoolService.deleteSchool(schoolId);
        return ResponseEntity.ok(Map.of("message", "School deleted successfully"));
    }

    @PostMapping("/{schoolId}/suspend")
    public ResponseEntity<Map<String, String>> suspendSchool(
            @PathVariable String schoolId,
            @RequestParam String reason) {
        
        schoolService.suspendSchool(schoolId, reason);
        return ResponseEntity.ok(Map.of("message", "School suspended successfully"));
    }

    @PostMapping("/{schoolId}/activate")
    public ResponseEntity<Map<String, String>> activateSchool(@PathVariable String schoolId) {
        schoolService.activateSchool(schoolId);
        return ResponseEntity.ok(Map.of("message", "School activated successfully"));
    }

    @PostMapping("/{schoolId}/backup")
    public ResponseEntity<Map<String, String>> backupSchool(@PathVariable String schoolId) {
        String backupPath = schoolService.backupSchool(schoolId);
        return ResponseEntity.ok(Map.of("message", "Backup completed", "path", backupPath));
    }

    @PostMapping("/{schoolId}/restore")
    public ResponseEntity<Map<String, String>> restoreSchool(
            @PathVariable String schoolId,
            @RequestParam String backupPath) {
        
        schoolService.restoreSchool(schoolId, backupPath);
        return ResponseEntity.ok(Map.of("message", "School restored successfully"));
    }

    @GetMapping("/{schoolId}/stats")
    public ResponseEntity<Map<String, Object>> getSchoolStats(@PathVariable String schoolId) {
        Map<String, Object> stats = schoolService.getSchoolStats(schoolId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{schoolId}/users")
    public ResponseEntity<List<Map<String, Object>>> getSchoolUsers(@PathVariable String schoolId) {
        List<Map<String, Object>> users = schoolService.getSchoolUsers(schoolId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{schoolId}/activity")
    public ResponseEntity<List<Map<String, Object>>> getSchoolActivity(@PathVariable String schoolId) {
        List<Map<String, Object>> activity = schoolService.getSchoolActivity(schoolId);
        return ResponseEntity.ok(activity);
    }

    @PostMapping("/{schoolId}/send-welcome-email")
    public ResponseEntity<Map<String, String>> sendWelcomeEmail(@PathVariable String schoolId) {
        schoolService.sendWelcomeEmail(schoolId);
        return ResponseEntity.ok(Map.of("message", "Welcome email sent successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SchoolDTO>> searchSchools(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<SchoolDTO> schools = schoolService.searchSchools(query, page, size);
        return ResponseEntity.ok(schools);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getSchoolCounts() {
        Map<String, Long> counts = schoolService.getSchoolCounts();
        return ResponseEntity.ok(counts);
    }
    
    // User Management Endpoints (Mock Implementation)
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        try {
            List<Map<String, Object>> allUsers = schoolService.getAllSchoolUsers();
            return ResponseEntity.ok(allUsers);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of(Map.of("error", e.getMessage())));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> request) {
        try {
            String schoolId = (String) request.get("schoolId");
            if (schoolId != null && !schoolId.isEmpty()) {
                // Create user in the specific school's database
                Map<String, Object> user = schoolService.createSchoolUser(schoolId, request);
                return ResponseEntity.ok(user);
            } else {
                // Create system admin user
                Map<String, Object> user = Map.of(
                    "id", System.currentTimeMillis(),
                    "username", request.get("username"),
                    "email", request.get("email"),
                    "role", request.get("role"),
                    "status", "ACTIVE",
                    "createdAt", java.time.Instant.now().toString()
                );
                return ResponseEntity.ok(user);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<Map<String, Object>> suspendUser(@PathVariable Long userId) {
        Map<String, Object> user = Map.of(
            "id", userId,
            "status", "SUSPENDED"
        );
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{userId}/activate")
    public ResponseEntity<Map<String, Object>> activateUser(@PathVariable Long userId) {
        Map<String, Object> user = Map.of(
            "id", userId,
            "status", "ACTIVE"
        );
        return ResponseEntity.ok(user);
    }
}

