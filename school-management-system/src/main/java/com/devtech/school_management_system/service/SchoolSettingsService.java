package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.SchoolSettings;
import com.devtech.school_management_system.repository.SchoolSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SchoolSettingsService {

    private final SchoolSettingsRepository schoolSettingsRepository;

    @Autowired
    public SchoolSettingsService(SchoolSettingsRepository schoolSettingsRepository) {
        this.schoolSettingsRepository = schoolSettingsRepository;
    }

    public SchoolSettings getSchoolSettings() {
        SchoolSettings settings = schoolSettingsRepository.findFirstByOrderByIdAsc();
        if (settings == null) {
            // Create default settings if none exist
            settings = new SchoolSettings();
            settings.setSchoolName("School Name");
            schoolSettingsRepository.save(settings);
        }
        return settings;
    }

    public SchoolSettings updateSchoolSettings(SchoolSettings updatedSettings) {
        SchoolSettings existingSettings = getSchoolSettings();
        
        // Update fields
        existingSettings.setSchoolName(updatedSettings.getSchoolName());
        existingSettings.setSchoolAddress(updatedSettings.getSchoolAddress());
        existingSettings.setSchoolPhone(updatedSettings.getSchoolPhone());
        existingSettings.setSchoolEmail(updatedSettings.getSchoolEmail());
        existingSettings.setSchoolLogoUrl(updatedSettings.getSchoolLogoUrl());
        existingSettings.setMinistryLogoUrl(updatedSettings.getMinistryLogoUrl());
        existingSettings.setPrincipalName(updatedSettings.getPrincipalName());
        existingSettings.setPrincipalSignatureUrl(updatedSettings.getPrincipalSignatureUrl());
        existingSettings.setReportHeaderText(updatedSettings.getReportHeaderText());
        existingSettings.setReportFooterText(updatedSettings.getReportFooterText());
        
        return schoolSettingsRepository.save(existingSettings);
    }
}