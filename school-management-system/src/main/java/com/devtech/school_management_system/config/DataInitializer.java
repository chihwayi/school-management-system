package com.devtech.school_management_system.config;

import com.devtech.school_management_system.entity.Role;
import com.devtech.school_management_system.entity.Section;
import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.enums.ERole;
import com.devtech.school_management_system.enums.SubjectCategory;
import com.devtech.school_management_system.repository.RoleRepository;
import com.devtech.school_management_system.repository.SectionRepository;
import com.devtech.school_management_system.repository.SubjectRepository;
import com.devtech.school_management_system.repository.TeacherRepository;
import com.devtech.school_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.Arrays;
import java.util.List;

import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Guardian;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.repository.GuardianRepository;

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

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SectionRepository sectionRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private GuardianRepository guardianRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
    }

    private void initializeRoles() {
        for (ERole roleEnum : ERole.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role(roleEnum);
                roleRepository.save(role);
            }
        }
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@school.com", passwordEncoder.encode("admin123"));
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            Role teacherRole = roleRepository.findByName(ERole.ROLE_TEACHER)
                    .orElseThrow(() -> new RuntimeException("Teacher role not found"));
            admin.setRoles(Set.of(adminRole, teacherRole));
            userRepository.save(admin);
            
            // Create teacher record for admin
            Teacher adminTeacher = new Teacher();
            adminTeacher.setFirstName("System");
            adminTeacher.setLastName("Administrator");
            adminTeacher.setEmployeeId("ADMIN001");
            adminTeacher.setUser(admin);
            adminTeacher.setCreatedAt(LocalDateTime.now());
            adminTeacher.setUpdatedAt(LocalDateTime.now());
            teacherRepository.save(adminTeacher);
        }
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