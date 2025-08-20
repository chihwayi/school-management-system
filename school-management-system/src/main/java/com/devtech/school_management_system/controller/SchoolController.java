package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.SchoolConfigDTO;
import com.devtech.school_management_system.entity.School;
import com.devtech.school_management_system.service.SchoolServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/school", produces = MediaType.APPLICATION_JSON_VALUE)
public class SchoolController {

    private final SchoolServiceImpl schoolService;

    @Autowired
    public SchoolController(SchoolServiceImpl schoolService) {
        this.schoolService = schoolService;
    }

    @Autowired
    private com.devtech.school_management_system.service.TenantManagementService tenantManagementService;

    @GetMapping("/config")
    public ResponseEntity<?> getSchoolConfig() {
        try {
            // Get tenant from context (set by TenantInterceptor)
            String currentTenant = com.devtech.school_management_system.config.TenantContext.getCurrentTenant();
            String currentDatabase = com.devtech.school_management_system.config.TenantContext.getCurrentDatabase();
            System.out.println("SchoolController.getSchoolConfig() - Tenant: " + currentTenant + ", Database: " + currentDatabase);
            
            // Check if we're in a multi-tenant context
            boolean tenantConfigured = false;
            if (currentTenant != null && !"default".equals(currentTenant)) {
                tenantConfigured = tenantManagementService.checkAndConfigureTenantDatabase();
            }
            
            boolean isConfigured = schoolService.isSchoolConfigured();

            Map<String, Object> response = new HashMap<>();
            response.put("configured", isConfigured);
            response.put("tenant", currentTenant != null ? currentTenant : "default");
            response.put("database", currentDatabase != null ? currentDatabase : "school_management_system");
            response.put("tenantConfigured", tenantConfigured);

            if (isConfigured) {
                School school = schoolService.getSchoolConfiguration();
                response.put("school", school);
            } else {
                response.put("school", null);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("configured", false);
            response.put("school", null);
            response.put("tenant", "error");
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping(value = "/setup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> setupSchool(
            @RequestPart("schoolConfig") SchoolConfigDTO schoolConfigDTO,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "background", required = false) MultipartFile background) {

        try {
            School school = schoolService.setupSchool(schoolConfigDTO, logo, background);
            return ResponseEntity.ok(school);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to upload files: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping(value = "/setup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setupSchoolJson(@RequestBody SchoolConfigDTO schoolConfigDTO) {
        try {
            School school = schoolService.setupSchool(schoolConfigDTO, null, null);
            return ResponseEntity.ok(school);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSchool(
            @PathVariable Long id,
            @RequestPart("schoolConfig") SchoolConfigDTO schoolConfigDTO,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "background", required = false) MultipartFile background) {

        try {
            School school = schoolService.updateSchool(id, schoolConfigDTO, logo, background);
            return ResponseEntity.ok(school);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to upload files: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}

