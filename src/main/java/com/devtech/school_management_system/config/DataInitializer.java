package com.devtech.school_management_system.config;

import com.devtech.school_management_system.entity.Role;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.enums.ERole;
import com.devtech.school_management_system.repository.RoleRepository;
import com.devtech.school_management_system.repository.TeacherRepository;
import com.devtech.school_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeDefaultUsers();
        initializeDefaultTeacher();
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

    private void initializeDefaultTeacher() {
        User teacherUser = userRepository.findByUsername("teacher").orElse(null);
        if (teacherUser != null && !teacherRepository.findByUserUsername("teacher").isPresent()) {
            Teacher teacher = new Teacher();
            teacher.setFirstName("Default");
            teacher.setLastName("Teacher");
            teacher.setEmployeeId("TEACH001");
            teacher.setUser(teacherUser);
            teacher.setCreatedAt(LocalDateTime.now());
            teacher.setUpdatedAt(LocalDateTime.now());
            teacherRepository.save(teacher);
        }
    }
}