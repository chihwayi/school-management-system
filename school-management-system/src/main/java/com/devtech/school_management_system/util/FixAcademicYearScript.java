package com.devtech.school_management_system.util;

import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class FixAcademicYearScript {

    @Bean
    @Profile("fix-data")
    public CommandLineRunner fixAcademicYears(StudentRepository studentRepository) {
        return args -> {
            System.out.println("Starting academic year fix script...");
            
            // Get all students
            List<Student> students = studentRepository.findAll();
            
            for (Student student : students) {
                // Standardize academic year format to match class groups
                if ("2024-2025".equals(student.getAcademicYear())) {
                    student.setAcademicYear("2025");
                    studentRepository.save(student);
                    System.out.println("Updated student " + student.getId() + " academic year from 2024-2025 to 2025");
                }
            }
            
            System.out.println("Academic year fix script completed.");
        };
    }
}