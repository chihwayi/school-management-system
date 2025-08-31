package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.service.AssignmentDocumentService;
import com.devtech.school_management_system.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/assignments", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasRole('TEACHER')")
public class AssignmentController {

    @Autowired
    private AssignmentDocumentService assignmentDocumentService;

    @Autowired
    private TeacherService teacherService;

    /**
     * Upload assignment document and distribute to class
     */
    @PostMapping("/upload-and-distribute")
    public ResponseEntity<Map<String, String>> uploadAndDistributeAssignment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("subjectName") String subjectName,
            @RequestParam("form") String form,
            @RequestParam("section") String section,
            @RequestParam("academicYear") String academicYear) {

        try {
            Teacher teacher = getCurrentTeacher();
            
            assignmentDocumentService.uploadAndDistributeAssignment(
                file, title, subjectName, form, section, academicYear, teacher
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Assignment document uploaded and distributed successfully to class " + form + " " + section);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload and distribute assignment: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Distribute AI-generated assignment content to class
     */
    @PostMapping("/distribute-ai")
    public ResponseEntity<Map<String, String>> distributeAIAssignment(
            @RequestParam("content") String content,
            @RequestParam("title") String title,
            @RequestParam("subjectName") String subjectName,
            @RequestParam("form") String form,
            @RequestParam("section") String section,
            @RequestParam("academicYear") String academicYear) {

        try {
            Teacher teacher = getCurrentTeacher();
            
            assignmentDocumentService.distributeAIAssignment(
                content, title, subjectName, form, section, academicYear, teacher
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "AI-generated assignment distributed successfully to class " + form + " " + section);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to distribute AI assignment: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get current authenticated teacher
     */
    private Teacher getCurrentTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return teacherService.getTeacherByUsername(username);
    }
}
