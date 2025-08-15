package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.School;
import com.devtech.school_management_system.service.SchoolService;
import com.devtech.school_management_system.service.SchoolServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/ministry-logo")
public class MinistryLogoController {

    @Autowired
    private SchoolService schoolService;
    
    @Autowired
    private SchoolServiceImpl schoolServiceImpl;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadMinistryLogo(@RequestParam("file") MultipartFile file) {
        try {
            School school = schoolService.getSchoolConfiguration();
            if (school == null) {
                return ResponseEntity.badRequest().body("School not configured");
            }

            // Create a DTO from existing school data
            com.devtech.school_management_system.dto.SchoolConfigDTO dto = new com.devtech.school_management_system.dto.SchoolConfigDTO();
            dto.setName(school.getName());
            dto.setDescription(school.getDescription());
            dto.setPrimaryColor(school.getPrimaryColor());
            dto.setSecondaryColor(school.getSecondaryColor());
            dto.setContactEmail(school.getContactEmail());
            dto.setContactPhone(school.getContactPhone());
            dto.setAddress(school.getAddress());
            dto.setWebsite(school.getWebsite());
            
            School updatedSchool = schoolServiceImpl.updateSchool(
                school.getId(), 
                dto,
                null, // No logo update
                null, // No background update
                file  // Ministry logo update
            );

            return ResponseEntity.ok(updatedSchool.getMinistryLogoPath());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload ministry logo");
        }
    }

    @GetMapping("/current")
    public ResponseEntity<String> getCurrentMinistryLogo() {
        School school = schoolService.getSchoolConfiguration();
        if (school != null && school.getMinistryLogoPath() != null) {
            return ResponseEntity.ok(school.getMinistryLogoPath());
        }
        return ResponseEntity.notFound().build();
    }
}