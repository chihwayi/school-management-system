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
        initializeDefaultUsers();
        initializeDefaultTeacher();
        initializeDefaultSubjects();
        initializeDefaultSections();
        initializeStudentsWithGuardians();
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
    
    private void initializeStudentsWithGuardians() {
        // Check if students already exist
        if (studentRepository.count() > 0) {
            return; // Skip if students already exist
        }
        
        // Lists for random data generation
        List<String> firstNames = Arrays.asList(
            "John", "Emma", "Michael", "Sophia", "William", "Olivia", "James", "Ava", 
            "Alexander", "Isabella", "Benjamin", "Mia", "Elijah", "Charlotte", "Lucas", 
            "Amelia", "Mason", "Harper", "Ethan", "Evelyn", "Daniel", "Abigail", "Matthew", 
            "Emily", "David", "Elizabeth", "Joseph", "Sofia", "Jackson", "Avery"
        );
        
        List<String> lastNames = Arrays.asList(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", 
            "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", 
            "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", 
            "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson"
        );
        
        List<String> guardianFirstNames = Arrays.asList(
            "Robert", "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", 
            "Jessica", "Sarah", "Karen", "Nancy", "Lisa", "Betty", "Margaret", "Sandra", 
            "Ashley", "Kimberly", "Richard", "Joseph", "Thomas", "Charles", "Christopher", 
            "Daniel", "Matthew", "Anthony", "Mark", "Donald", "Steven", "Paul"
        );
        
        List<String> relationships = Arrays.asList(
            "Father", "Mother", "Guardian", "Grandfather", "Grandmother", "Uncle", "Aunt"
        );
        
        List<String> sections = Arrays.asList("A", "B", "C");
        String academicYear = "2025";
        
        Random random = new Random();
        
        // Create 20 students with guardians
        for (int i = 1; i <= 20; i++) {
            // Generate random form (1-6)
            int form = random.nextInt(6) + 1;
            String formStr = "Form " + form;
            
            // Determine level based on form
            String level;
            if (form <= 2) {
                level = "JUNIOR_SECONDARY";
            } else if (form <= 4) {
                level = "O_LEVEL";
            } else {
                level = "A_LEVEL";
            }
            
            // Generate random section
            String section = sections.get(random.nextInt(sections.size()));
            
            // Create student
            Student student = new Student();
            student.setFirstName(firstNames.get(random.nextInt(firstNames.size())));
            student.setLastName(lastNames.get(random.nextInt(lastNames.size())));
            student.setStudentId("STU" + String.format("%04d", i));
            student.setForm(formStr);
            student.setSection(section);
            student.setLevel(level);
            student.setAcademicYear(academicYear);
            student.setEnrollmentDate(LocalDate.now().minusDays(random.nextInt(365)));
            
            Student savedStudent = studentRepository.save(student);
            
            // Create 1-2 guardians for each student
            int numGuardians = random.nextInt(2) + 1;
            for (int j = 0; j < numGuardians; j++) {
                Guardian guardian = new Guardian();
                guardian.setStudent(savedStudent);
                guardian.setName(guardianFirstNames.get(random.nextInt(guardianFirstNames.size())) + " " + 
                                savedStudent.getLastName());
                guardian.setRelationship(relationships.get(random.nextInt(relationships.size())));
                
                // Generate phone number
                String phoneNumber = "+263" + (70 + random.nextInt(30)) + 
                                    String.format("%07d", random.nextInt(10000000));
                guardian.setPhoneNumber(phoneNumber);
                
                // 70% chance to have WhatsApp
                if (random.nextDouble() < 0.7) {
                    guardian.setWhatsappNumber(phoneNumber);
                }
                
                // First guardian is primary
                guardian.setPrimaryGuardian(j == 0);
                
                guardianRepository.save(guardian);
            }
            
            System.out.println("Created student: " + student.getFirstName() + " " + student.getLastName() + 
                              " in " + student.getForm() + " " + student.getSection());
        }
        
        System.out.println("Successfully created 20 students with guardians");
    }
}