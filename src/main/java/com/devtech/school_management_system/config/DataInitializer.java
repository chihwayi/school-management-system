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

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeDefaultUsers();
        initializeDefaultTeacher();
        initializeDefaultSubjects();
        initializeDefaultSections();
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

    private void initializeDefaultSubjects() {
        if (subjectRepository.count() == 0) {
            // Junior Secondary Level subjects
            createSubject("English", "JS-ENG", SubjectCategory.JUNIOR_SECONDARY_LANGUAGES, "JUNIOR_SECONDARY");
            createSubject("Mathematics", "JS-MATH", SubjectCategory.JUNIOR_SECONDARY_SCIENCES, "JUNIOR_SECONDARY");
            createSubject("Integrated Science", "JS-SCI", SubjectCategory.JUNIOR_SECONDARY_SCIENCES, "JUNIOR_SECONDARY");
            createSubject("Social Studies", "JS-SOC", SubjectCategory.JUNIOR_SECONDARY_ARTS, "JUNIOR_SECONDARY");
            
            // O Level subjects
            createSubject("English", "ENG", SubjectCategory.O_LEVEL_LANGUAGES, "O_LEVEL");
            createSubject("Mathematics", "MATH", SubjectCategory.O_LEVEL_SCIENCES, "O_LEVEL");
            createSubject("Combined Science", "CSCI", SubjectCategory.O_LEVEL_SCIENCES, "O_LEVEL");
            createSubject("History", "HIST", SubjectCategory.O_LEVEL_ARTS, "O_LEVEL");
            createSubject("Geography", "GEOG", SubjectCategory.O_LEVEL_SCIENCES, "O_LEVEL");
            
            // A Level subjects
            createSubject("Pure Mathematics", "PMATH", SubjectCategory.A_LEVEL_SCIENCES, "A_LEVEL");
            createSubject("Physics", "PHYS", SubjectCategory.A_LEVEL_SCIENCES, "A_LEVEL");
            createSubject("Chemistry", "CHEM", SubjectCategory.A_LEVEL_SCIENCES, "A_LEVEL");
            createSubject("Biology", "BIO", SubjectCategory.A_LEVEL_SCIENCES, "A_LEVEL");
            createSubject("Economics", "ECON", SubjectCategory.A_LEVEL_COMMERCIALS, "A_LEVEL");
        }
    }

    private void createSubject(String name, String code, SubjectCategory category, String level) {
        Subject subject = new Subject();
        subject.setName(name);
        subject.setCode(code);
        subject.setCategory(category);
        subject.setLevel(level);
        subject.setDescription("Default " + name + " subject");
        subjectRepository.save(subject);
    }

    private void initializeDefaultSections() {
        if (sectionRepository.count() == 0) {
            createSection("A", "Section A - Alphabetic naming");
            createSection("B", "Section B - Alphabetic naming");
            createSection("C", "Section C - Alphabetic naming");
        }
    }

    private void createSection(String name, String description) {
        Section section = new Section();
        section.setName(name);
        section.setDescription(description);
        section.setActive(true);
        sectionRepository.save(section);
    }
}