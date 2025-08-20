package com.devtech.admin.service;

import com.devtech.admin.dto.LoginResponse;
import com.devtech.admin.entity.AdminUser;
import com.devtech.admin.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse authenticate(String username, String password) {
        Optional<AdminUser> userOpt = adminUserRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            AdminUser user = userOpt.get();
            
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                // Generate a simple token (in production, use JWT)
                String token = "admin-token-" + System.currentTimeMillis();
                
                return new LoginResponse(token, user.getRole(), "Login successful");
            } else {
                throw new RuntimeException("Invalid password");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public boolean validateToken(String token) {
        // Simple token validation (in production, validate JWT)
        return token != null && token.startsWith("admin-token-");
    }
}
