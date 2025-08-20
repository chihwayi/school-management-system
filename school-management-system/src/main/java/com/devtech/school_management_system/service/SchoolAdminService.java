package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.LoginRequest;
import com.devtech.school_management_system.dto.LoginResponse;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.repository.UserRepository;
import com.devtech.school_management_system.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SchoolAdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TenantManagementService tenantManagementService;

    /**
     * School admin login
     */
    public LoginResponse login(LoginRequest loginRequest) {
        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check if user is admin
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || "SUPER_ADMIN".equals(role.getName()));

        if (!isAdmin) {
            throw new RuntimeException("Access denied. Admin privileges required.");
        }

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getUsername());

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                "ADMIN"
        );
    }

    /**
     * Get school admin dashboard
     */
    public Map<String, Object> getDashboard() {
        String currentTenant = tenantManagementService.getCurrentTenant();
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("tenantId", currentTenant);
        dashboard.put("schoolName", "School " + currentTenant);
        dashboard.put("totalStudents", 0);
        dashboard.put("totalTeachers", 0);
        dashboard.put("totalClasses", 0);
        dashboard.put("totalUsers", 0);
        dashboard.put("lastLogin", System.currentTimeMillis());
        
        return dashboard;
    }

    /**
     * Get school configuration
     */
    public Map<String, Object> getSchoolConfig() {
        String currentTenant = tenantManagementService.getCurrentTenant();
        
        Map<String, Object> config = new HashMap<>();
        config.put("schoolName", "School " + currentTenant);
        config.put("primaryColor", "#3B82F6");
        config.put("secondaryColor", "#1E40AF");
        config.put("contactEmail", "admin@" + currentTenant + ".com");
        config.put("contactPhone", "");
        config.put("address", "");
        config.put("timezone", "UTC");
        config.put("currency", "USD");
        config.put("language", "en");
        
        return config;
    }

    /**
     * Update school configuration
     */
    public Map<String, Object> updateSchoolConfig(Map<String, Object> config) {
        // This would update the school configuration in the tenant database
        // For now, just return the updated config
        return config;
    }
}
