package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.service.StudentImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentImportController {

    @Autowired
    private StudentImportService studentImportService;

    @GetMapping("/template")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLERK')")
    public ResponseEntity<Resource> downloadTemplate() {
        try {
            Resource resource = studentImportService.generateTemplate();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"student_import_template.xlsx\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLERK')")
    public ResponseEntity<Map<String, Object>> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = studentImportService.importStudents(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Import failed: " + e.getMessage()));
        }
    }
}