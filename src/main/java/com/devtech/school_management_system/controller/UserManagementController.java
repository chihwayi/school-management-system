package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.EmailUpdateDTO;
import com.devtech.school_management_system.dto.PasswordResetDTO;
import com.devtech.school_management_system.dto.RoleUpdateDTO;
import com.devtech.school_management_system.dto.UserDTO;
import com.devtech.school_management_system.dto.UserRegistrationDTO;
import com.devtech.school_management_system.enums.ERole;
import com.devtech.school_management_system.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        try {
            UserDTO createdUser = userService.createUser(userRegistrationDTO);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            UserDTO updatedUser = userService.resetPassword(
                    passwordResetDTO.getUsername(),
                    passwordResetDTO.getNewPassword()
            );
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmail(@RequestBody EmailUpdateDTO emailUpdateDTO) {
        try {
            UserDTO updatedUser = userService.updateEmail(
                    emailUpdateDTO.getUsername(),
                    emailUpdateDTO.getNewEmail()
            );
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRoles(@RequestBody RoleUpdateDTO roleUpdateDTO) {
        try {
            // Log the incoming roles for debugging
            System.out.println("Updating roles for user: " + roleUpdateDTO.getUsername());
            System.out.println("Roles received: " + roleUpdateDTO.getRoles());
            
            UserDTO updatedUser = userService.updateRoles(
                    roleUpdateDTO.getUsername(),
                    roleUpdateDTO.getRoles()
            );
            
            // Log the updated user for debugging
            System.out.println("Updated user: " + updatedUser.getUsername());
            System.out.println("Updated roles: " + updatedUser.getRoles());
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.out.println("Error updating roles: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/toggle-status/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable String username) {
        try {
            UserDTO updatedUser = userService.toggleUserStatus(username);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/available-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAvailableRoles() {
        try {
            Map<String, String> roles = new HashMap<>();
            for (ERole role : ERole.values()) {
                roles.put(role.name(), role.name().replace("ROLE_", ""));
            }
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}