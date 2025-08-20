package com.devtech.admin.service;

import com.devtech.admin.dto.UserCreateRequest;
import com.devtech.admin.dto.UserDTO;
import com.devtech.admin.entity.AdminUser;
import com.devtech.admin.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return adminUserRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO createUser(UserCreateRequest request) {
        if (adminUserRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (adminUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        AdminUser user = new AdminUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        AdminUser savedUser = adminUserRepository.save(user);
        return new UserDTO(savedUser);
    }

    public void deleteUser(Long userId) {
        AdminUser user = adminUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if ("SUPER_ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Cannot delete super admin user");
        }
        
        adminUserRepository.delete(user);
    }

    public UserDTO suspendUser(Long userId) {
        AdminUser user = adminUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus("SUSPENDED");
        user.setUpdatedAt(LocalDateTime.now());
        
        AdminUser savedUser = adminUserRepository.save(user);
        return new UserDTO(savedUser);
    }

    public UserDTO activateUser(Long userId) {
        AdminUser user = adminUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus("ACTIVE");
        user.setUpdatedAt(LocalDateTime.now());
        
        AdminUser savedUser = adminUserRepository.save(user);
        return new UserDTO(savedUser);
    }
}