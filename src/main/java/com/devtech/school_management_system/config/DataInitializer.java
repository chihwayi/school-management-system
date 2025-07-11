package com.devtech.school_management_system.config;

import com.devtech.school_management_system.entity.Role;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.enums.ERole;
import com.devtech.school_management_system.repository.RoleRepository;
import com.devtech.school_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeDefaultUsers();
    }

    private void initializeRoles() {
        for (ERole roleEnum : ERole.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role(roleEnum);
                roleRepository.save(role);
            }
        }
    }

    private void initializeDefaultUsers() {
        createUserIfNotExists("admin", "admin@school.com", "admin123", ERole.ROLE_ADMIN);
        createUserIfNotExists("clerk", "clerk@school.com", "clerk123", ERole.ROLE_CLERK);
        createUserIfNotExists("teacher", "teacher@school.com", "teacher123", ERole.ROLE_TEACHER);
    }

    private void createUserIfNotExists(String username, String email, String password, ERole roleEnum) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User(username, email, passwordEncoder.encode(password));
            Role role = roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleEnum));
            user.setRoles(Set.of(role));
            userRepository.save(user);
        }
    }
}