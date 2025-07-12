package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.LoginRequest;
import com.devtech.school_management_system.dto.RegisterRequest;
import com.devtech.school_management_system.entity.Role;
import com.devtech.school_management_system.entity.School;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.enums.ERole;
import com.devtech.school_management_system.repository.UserRepository;
import com.devtech.school_management_system.security.JwtTokenProvider;
import com.devtech.school_management_system.service.RoleService;
import com.devtech.school_management_system.service.SchoolServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SchoolServiceImpl schoolService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder,
                          SchoolServiceImpl schoolService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.schoolService = schoolService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            // Extract roles from authentication
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            // Get the username from the authentication object
            String username = authentication.getName();

            // Build response
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", token);
            responseMap.put("roles", roles);
            responseMap.put("username", username);


            return ResponseEntity.ok(responseMap);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // Check if school is configured before allowing registration
        if (!schoolService.isSchoolConfigured()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "School setup is required before registration");
            response.put("setupRequired", true);
            return ResponseEntity.status(412).body(response);
        }

        // Check if the username is already taken
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Username is already taken");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if the email is already registered
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Email is already registered");
            return ResponseEntity.badRequest().body(response);
        }

        // Handle roles from the request
        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Default role assignment
            Role userRole = roleService.getRoleByName(ERole.ROLE_USER);
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleService.getRoleByName(ERole.ROLE_ADMIN);
                    roles.add(adminRole);
                } else {
                    Role defaultRole = roleService.getRoleByName(ERole.ROLE_USER);
                    roles.add(defaultRole);
                }
            });
        }

        // Create and save user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setRoles(roles);
        user.setEnabled(true);
        userRepository.save(user);

        // Return a success response in JSON format
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-school-setup")
    public ResponseEntity<?> checkSchoolSetup() {
        boolean isConfigured = schoolService.isSchoolConfigured();

        Map<String, Object> response = new HashMap<>();
        response.put("configured", isConfigured);

        if (isConfigured) {
            School school = schoolService.getSchoolConfiguration();
            Map<String, Object> schoolInfo = new HashMap<>();
            schoolInfo.put("name", school.getName());
            schoolInfo.put("logoPath", school.getLogoPath());
            schoolInfo.put("backgroundPath", school.getBackgroundPath());
            response.put("school", schoolInfo);
        }

        return ResponseEntity.ok(response);
    }
}

