package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.SignatureDTO;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.repository.TeacherRepository;
import com.devtech.school_management_system.repository.TeacherSubjectClassRepository;
import com.devtech.school_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SignatureService {

    private final TeacherRepository teacherRepository;
    private final TeacherSubjectClassRepository teacherSubjectClassRepository;
    private final UserRepository userRepository;
    private final String uploadDir = "uploads/signatures/";

    @Autowired
    public SignatureService(TeacherRepository teacherRepository,
                           TeacherSubjectClassRepository teacherSubjectClassRepository,
                           UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.teacherSubjectClassRepository = teacherSubjectClassRepository;
        this.userRepository = userRepository;
        
        // Create upload directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public SignatureDTO uploadSignature(MultipartFile file, String username) {
        try {
            Teacher teacher = teacherRepository.findByUserUsername(username)
                    .orElse(null);
            
            if (teacher == null) {
                // Auto-create teacher record for admin users
                System.out.println("Creating teacher record for user: " + username);
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username));
                
                teacher = new Teacher();
                teacher.setUser(user);
                teacher.setFirstName(user.getUsername()); // Use username as placeholder
                teacher.setLastName("Administrator");
                teacher.setEmployeeId("EMP_" + user.getId()); // Generate unique employee ID
                
                try {
                    teacher = teacherRepository.save(teacher);
                    System.out.println("Teacher record created successfully with ID: " + teacher.getId());
                } catch (Exception e) {
                    System.err.println("Error creating teacher record: " + e.getMessage());
                    throw new RuntimeException("Failed to create teacher record: " + e.getMessage());
                }
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".png";
            String filename = "signature_" + teacher.getId() + "_" + UUID.randomUUID() + extension;
            
            // Save file
            Path filePath = Paths.get(uploadDir + filename);
            Files.write(filePath, file.getBytes());
            
            // Update teacher signature URL
            String signatureUrl = "/uploads/signatures/" + filename;
            teacher.setSignatureUrl(signatureUrl);
            teacherRepository.save(teacher);
            
            return new SignatureDTO(
                teacher.getId(),
                signatureUrl,
                teacher.getFirstName() + " " + teacher.getLastName(),
                "TEACHER",
                LocalDateTime.now().toString()
            );
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload signature", e);
        }
    }

    public SignatureDTO getUserSignature(String username) {
        Teacher teacher = teacherRepository.findByUserUsername(username)
                .orElse(null);
        
        if (teacher == null) {
            // Handle admin users who might not have teacher records
            return null;
        }
        
        if (teacher.getSignatureUrl() == null) {
            return null;
        }
        
        return new SignatureDTO(
            teacher.getId(),
            teacher.getSignatureUrl(),
            teacher.getFirstName() + " " + teacher.getLastName(),
            "TEACHER",
            null
        );
    }

    public SignatureDTO getPrincipalSignature() {
        // For now, return null - would need proper admin/principal identification
        return null;
    }

    public SignatureDTO getClassTeacherSignature(String form, String section) {
        // For now, return null - would need proper class teacher identification
        return null;
    }

    public SignatureDTO getSubjectTeacherSignature(Long subjectId, String form, String section) {
        // For now, return null - would need proper subject teacher identification
        return null;
    }
}