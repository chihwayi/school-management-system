package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.UserDTO;
import com.devtech.school_management_system.entity.Role;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.enums.ERole;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User createTeacherUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();
        Role teacherRole = roleService.getRoleByName(ERole.ROLE_TEACHER);
        roles.add(teacherRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User createClerkUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();
        Role clerkRole = roleService.getRoleByName(ERole.ROLE_CLERK);
        roles.add(clerkRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User createAdminUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();
        Role adminRole = roleService.getRoleByName(ERole.ROLE_ADMIN);
        roles.add(adminRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToDTO(user);
    }
    
    public UserDTO resetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        
        return convertToDTO(updatedUser);
    }
    
    public UserDTO updateEmail(String username, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Email is already in use");
        }
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        user.setEmail(newEmail);
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        
        return convertToDTO(updatedUser);
    }
    
    public UserDTO updateRoles(String username, Set<String> roleNames) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            try {
                // Try to parse the role directly if it's in the format "ROLE_XXX"
                ERole eRole;
                if (roleName.startsWith("ROLE_")) {
                    eRole = ERole.valueOf(roleName);
                } else {
                    // If not in ROLE_XXX format, try to match by name
                    switch (roleName.toUpperCase()) {
                        case "ADMIN":
                            eRole = ERole.ROLE_ADMIN;
                            break;
                        case "CLERK":
                            eRole = ERole.ROLE_CLERK;
                            break;
                        case "TEACHER":
                            eRole = ERole.ROLE_TEACHER;
                            break;
                        case "CLASS_TEACHER":
                            eRole = ERole.ROLE_CLASS_TEACHER;
                            break;
                        default:
                            eRole = ERole.ROLE_USER;
                    }
                }
                Role role = roleService.getRoleByName(eRole);
                roles.add(role);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role name: " + roleName);
            }
        }
        
        user.setRoles(roles);
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        
        return convertToDTO(updatedUser);
    }
    
    public UserDTO toggleUserStatus(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        user.setEnabled(!user.isEnabled());
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        
        return convertToDTO(updatedUser);
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        dto.setRoles(roles);
        
        return dto;
    }
}
