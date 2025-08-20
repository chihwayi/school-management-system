package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.LoginRequest;
import com.devtech.school_management_system.dto.LoginResponse;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.service.SchoolAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/school-admin")
@CrossOrigin(origins = "*")
public class SchoolAdminController {

    @Autowired
    private SchoolAdminService schoolAdminService;

    /**
     * School admin login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = schoolAdminService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get school admin dashboard info
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = schoolAdminService.getDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get school configuration
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getSchoolConfig() {
        Map<String, Object> config = schoolAdminService.getSchoolConfig();
        return ResponseEntity.ok(config);
    }

    /**
     * Update school configuration
     */
    @PutMapping("/config")
    public ResponseEntity<Map<String, Object>> updateSchoolConfig(@RequestBody Map<String, Object> config) {
        Map<String, Object> updatedConfig = schoolAdminService.updateSchoolConfig(config);
        return ResponseEntity.ok(updatedConfig);
    }
}
