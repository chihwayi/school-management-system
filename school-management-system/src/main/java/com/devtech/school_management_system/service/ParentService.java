package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class ParentService {
    
    private final StudentRepository studentRepository;

    public ParentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
    }
}
