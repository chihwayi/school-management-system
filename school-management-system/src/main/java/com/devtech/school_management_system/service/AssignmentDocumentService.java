package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class AssignmentDocumentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private WhatsAppService whatsAppService;

    @Value("${file.upload.directory:./uploads}")
    private String uploadDirectory;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Upload assignment document and distribute to class
     */
    public void uploadAndDistributeAssignment(
            MultipartFile file,
            String title,
            String subjectName,
            String form,
            String section,
            String academicYear,
            Teacher teacher) throws IOException {

        // Save the document
        String documentPath = saveAssignmentDocument(file, title, teacher);
        String documentUrl = baseUrl + "/api/uploads/" + documentPath;

        // Get all students in the class
        List<Student> students = studentRepository.findByFormAndSectionAndAcademicYear(form, section, academicYear);

        // Send to students with WhatsApp numbers
        for (Student student : students) {
            if (student.getWhatsappNumber() != null && !student.getWhatsappNumber().trim().isEmpty()) {
                try {
                    whatsAppService.sendDocument(
                        student.getWhatsappNumber(),
                        createAssignmentMessage(subjectName, title, teacher.getFirstName() + " " + teacher.getLastName()),
                        documentUrl,
                        file.getOriginalFilename()
                    );
                } catch (Exception e) {
                    // Log error but continue with other students
                    System.err.println("Failed to send assignment to student " + student.getStudentId() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Distribute AI-generated assignment content to class
     */
    public void distributeAIAssignment(
            String content,
            String title,
            String subjectName,
            String form,
            String section,
            String academicYear,
            Teacher teacher) {

        // Get all students in the class
        List<Student> students = studentRepository.findByFormAndSectionAndAcademicYear(form, section, academicYear);

        // Send content to students with WhatsApp numbers
        for (Student student : students) {
            if (student.getWhatsappNumber() != null && !student.getWhatsappNumber().trim().isEmpty()) {
                try {
                    String message = createAIAssignmentMessage(subjectName, title, content, teacher.getFirstName() + " " + teacher.getLastName());
                    whatsAppService.sendMessage(student.getWhatsappNumber(), message);
                } catch (Exception e) {
                    // Log error but continue with other students
                    System.err.println("Failed to send AI assignment to student " + student.getStudentId() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Save assignment document to file system
     */
    private String saveAssignmentDocument(MultipartFile file, String title, Teacher teacher) throws IOException {
        // Create assignments directory
        Path assignmentsDir = Paths.get(uploadDirectory, "assignments");
        if (!Files.exists(assignmentsDir)) {
            Files.createDirectories(assignmentsDir);
        }

        // Generate unique filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String teacherId = teacher.getId().toString();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = String.format("assignment_%s_%s_%s%s", 
                                      title.replaceAll("[^a-zA-Z0-9]", "_"), 
                                      teacherId, timestamp, extension);

        // Save file
        Path filePath = assignmentsDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return "assignments/" + filename;
    }

    /**
     * Create assignment message for document distribution
     */
    private String createAssignmentMessage(String subjectName, String title, String teacherName) {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        return String.format(
            """
            📝 *New Assignment Document*
            
            *Subject:* %s
            *Title:* %s
            *Teacher:* %s
            *Date:* %s
            
            Please find the assignment document attached.
            
            Complete and submit by the due date.
            
            Good luck! 📚""",
            subjectName,
            title,
            teacherName,
            currentDate
        );
    }

    /**
     * Create AI assignment message for content distribution
     */
    private String createAIAssignmentMessage(String subjectName, String title, String content, String teacherName) {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        return String.format(
            """
            🤖 *AI-Generated Assignment*
            
            *Subject:* %s
            *Title:* %s
            *Teacher:* %s
            *Date:* %s
            
            *Assignment Content:*
            %s
            
            Complete and submit by the due date.
            
            Good luck! 📚""",
            subjectName,
            title,
            teacherName,
            currentDate,
            content
        );
    }
}
