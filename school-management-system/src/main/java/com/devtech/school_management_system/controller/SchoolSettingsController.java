package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.SchoolSettings;
import com.devtech.school_management_system.service.SchoolSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/school-settings", produces = MediaType.APPLICATION_JSON_VALUE)
public class SchoolSettingsController {

    private final SchoolSettingsService schoolSettingsService;

    @Autowired
    public SchoolSettingsController(SchoolSettingsService schoolSettingsService) {
        this.schoolSettingsService = schoolSettingsService;
    }

    @GetMapping
    public ResponseEntity<SchoolSettings> getSchoolSettings() {
        return ResponseEntity.ok(schoolSettingsService.getSchoolSettings());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchoolSettings> updateSchoolSettings(@RequestBody SchoolSettings schoolSettings) {
        return ResponseEntity.ok(schoolSettingsService.updateSchoolSettings(schoolSettings));
    }
}