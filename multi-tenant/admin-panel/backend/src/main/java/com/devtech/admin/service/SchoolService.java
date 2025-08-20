package com.devtech.admin.service;

import com.devtech.admin.dto.SchoolDTO;
import com.devtech.admin.dto.SchoolCreateRequest;
import com.devtech.admin.dto.SchoolUpdateRequest;
import com.devtech.admin.entity.School;
import com.devtech.admin.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SchoolManagementIntegrationService integrationService;

    public List<SchoolDTO> getAllSchools(int page, int size, String status, String planType) {
        List<School> schools;
        
        if (status != null && !status.equals("all")) {
            schools = schoolRepository.findByStatus(status);
        } else if (planType != null && !planType.equals("all")) {
            schools = schoolRepository.findByPlanType(planType);
        } else {
            schools = schoolRepository.findAll();
        }
        
        return schools.stream()
                .skip(page * size)
                .limit(size)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SchoolDTO getSchoolById(String schoolId) {
        Optional<School> school = schoolRepository.findBySchoolId(schoolId);
        if (school.isPresent()) {
            return convertToDTO(school.get());
        }
        throw new RuntimeException("School not found");
    }

    public SchoolDTO createSchool(SchoolCreateRequest request) {
        // Generate unique school ID
        String schoolId = "SCH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create database name (replace hyphens with underscores for MySQL compatibility)
        String databaseName = "school_" + request.getSubdomain().toLowerCase().replace("-", "_");
        
        School school = new School(
            schoolId,
            request.getName(),
            request.getSubdomain(),
            "yoursystem.com", // Default domain
            databaseName,
            request.getPlanType(),
            request.getAdminEmail(),
            request.getAdminUsername()
        );
        
        // Set additional fields
        school.setContactPerson(request.getContactPerson());
        school.setContactPhone(request.getContactPhone());
        school.setAddress(request.getAddress());
        school.setTimezone(request.getTimezone() != null ? request.getTimezone() : "Africa/Harare");
        school.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        school.setLanguage(request.getLanguage() != null ? request.getLanguage() : "en");
        school.setLogoUrl(request.getLogoUrl());
        school.setPrimaryColor(request.getPrimaryColor());
        school.setSecondaryColor(request.getSecondaryColor());
        school.setNotes(request.getNotes());
        
        School savedSchool = schoolRepository.save(school);
        
        // Create the school database and initialize it with the school management system
        try {
            integrationService.createSchoolDatabase(savedSchool);
        } catch (Exception e) {
            // Log the error but don't fail the school creation
            System.err.println("Failed to create school database: " + e.getMessage());
        }
        
        return convertToDTO(savedSchool);
    }

    public SchoolDTO updateSchool(String schoolId, SchoolUpdateRequest request) {
        Optional<School> schoolOpt = schoolRepository.findBySchoolId(schoolId);
        if (schoolOpt.isPresent()) {
            School school = schoolOpt.get();
            
            if (request.getName() != null) school.setName(request.getName());
            if (request.getPlanType() != null) school.setPlanType(request.getPlanType());
            if (request.getStatus() != null) school.setStatus(request.getStatus());
            if (request.getContactPerson() != null) school.setContactPerson(request.getContactPerson());
            if (request.getContactPhone() != null) school.setContactPhone(request.getContactPhone());
            if (request.getAddress() != null) school.setAddress(request.getAddress());
            if (request.getTimezone() != null) school.setTimezone(request.getTimezone());
            if (request.getCurrency() != null) school.setCurrency(request.getCurrency());
            if (request.getLanguage() != null) school.setLanguage(request.getLanguage());
            if (request.getLogoUrl() != null) school.setLogoUrl(request.getLogoUrl());
            if (request.getPrimaryColor() != null) school.setPrimaryColor(request.getPrimaryColor());
            if (request.getSecondaryColor() != null) school.setSecondaryColor(request.getSecondaryColor());
            if (request.getNotes() != null) school.setNotes(request.getNotes());
            
            school.setUpdatedAt(LocalDateTime.now());
            School savedSchool = schoolRepository.save(school);
            return convertToDTO(savedSchool);
        }
        throw new RuntimeException("School not found");
    }

    public void deleteSchool(String schoolId) {
        Optional<School> school = schoolRepository.findBySchoolId(schoolId);
        if (school.isPresent()) {
            School schoolToDelete = school.get();
            
            // Delete the school database
            try {
                integrationService.deleteSchoolDatabase(schoolToDelete.getDatabaseName());
            } catch (Exception e) {
                System.err.println("Failed to delete school database: " + e.getMessage());
            }
            
            // Delete the school record
            schoolRepository.delete(schoolToDelete);
        } else {
            throw new RuntimeException("School not found");
        }
    }

    public void suspendSchool(String schoolId, String reason) {
        Optional<School> school = schoolRepository.findBySchoolId(schoolId);
        if (school.isPresent()) {
            School s = school.get();
            s.setStatus("suspended");
            s.setNotes(s.getNotes() != null ? s.getNotes() + "\nSuspended: " + reason : "Suspended: " + reason);
            s.setUpdatedAt(LocalDateTime.now());
            schoolRepository.save(s);
        } else {
            throw new RuntimeException("School not found");
        }
    }

    public void activateSchool(String schoolId) {
        Optional<School> school = schoolRepository.findBySchoolId(schoolId);
        if (school.isPresent()) {
            School s = school.get();
            s.setStatus("active");
            s.setUpdatedAt(LocalDateTime.now());
            schoolRepository.save(s);
        } else {
            throw new RuntimeException("School not found");
        }
    }

    public String backupSchool(String schoolId) {
        // Mock backup implementation
        return "/backups/school_" + schoolId + "_" + System.currentTimeMillis() + ".sql";
    }

    public void restoreSchool(String schoolId, String backupPath) {
        // Mock restore implementation
        System.out.println("Restoring school " + schoolId + " from " + backupPath);
    }

    public Map<String, Object> getSchoolStats(String schoolId) {
        try {
            // Get real statistics from the school's database
            return integrationService.getRealSchoolStats(schoolId);
        } catch (Exception e) {
            // Fallback to mock statistics if there's an error
            return Map.of(
                "totalUsers", 0,
                "totalStudents", 0,
                "totalTeachers", 0,
                "totalClasses", 0,
                "error", "Unable to fetch real statistics: " + e.getMessage()
            );
        }
    }

    public List<Map<String, Object>> getSchoolUsers(String schoolId) {
        try {
            // Get real user data from the school's database
            return integrationService.getRealSchoolUsers(schoolId);
        } catch (Exception e) {
            // Fallback to empty list if there's an error
            return List.of();
        }
    }

    public List<Map<String, Object>> getSchoolActivity(String schoolId) {
        // Mock school activity
        return List.of(
            Map.of("action", "User Login", "user", "admin", "timestamp", LocalDateTime.now().minusMinutes(30)),
            Map.of("action", "Report Generated", "user", "teacher1", "timestamp", LocalDateTime.now().minusHours(1)),
            Map.of("action", "Payment Recorded", "user", "clerk1", "timestamp", LocalDateTime.now().minusHours(2))
        );
    }

    public void sendWelcomeEmail(String schoolId) {
        // Mock email sending
        System.out.println("Sending welcome email to school " + schoolId);
    }

    public List<SchoolDTO> searchSchools(String query, int page, int size) {
        List<School> schools = schoolRepository.searchSchools(query);
        return schools.stream()
                .skip(page * size)
                .limit(size)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getSchoolCounts() {
        long total = schoolRepository.count();
        long active = schoolRepository.countActiveSchools();
        long suspended = schoolRepository.countByStatus("suspended");
        long basic = schoolRepository.countByPlanType("basic");
        long premium = schoolRepository.countByPlanType("premium");
        long enterprise = schoolRepository.countByPlanType("enterprise");
        
        return Map.of(
            "total", total,
            "active", active,
            "suspended", suspended,
            "basic", basic,
            "premium", premium,
            "enterprise", enterprise
        );
    }

    public Map<String, Object> createSchoolUser(String schoolId, Map<String, Object> userRequest) {
        try {
            // Create user in the specific school's database
            return integrationService.createUserInSchoolDatabase(schoolId, userRequest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in school database: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getAllSchoolUsers() {
        try {
            System.out.println("SchoolService: Getting all school users...");
            List<Map<String, Object>> users = integrationService.getAllUsersFromAllSchools();
            System.out.println("SchoolService: Found " + users.size() + " users");
            return users;
        } catch (Exception e) {
            System.err.println("SchoolService: Error getting school users: " + e.getMessage());
            e.printStackTrace();
            throw e; // Don't catch silently
        }
    }

    public List<Map<String, Object>> queryDatabase(String query) {
        return integrationService.executeQuery(query);
    }

    private SchoolDTO convertToDTO(School school) {
        SchoolDTO dto = new SchoolDTO(
            school.getSchoolId(),
            school.getName(),
            school.getSubdomain(),
            school.getDomain(),
            school.getDatabaseName(),
            school.getPlanType(),
            school.getAdminEmail(),
            school.getAdminUsername()
        );
        
        dto.setStatus(school.getStatus());
        dto.setCreatedAt(school.getCreatedAt());
        dto.setUpdatedAt(school.getUpdatedAt());
        dto.setContactPerson(school.getContactPerson());
        dto.setContactPhone(school.getContactPhone());
        dto.setAddress(school.getAddress());
        dto.setTimezone(school.getTimezone());
        dto.setCurrency(school.getCurrency());
        dto.setLanguage(school.getLanguage());
        dto.setLogoUrl(school.getLogoUrl());
        dto.setPrimaryColor(school.getPrimaryColor());
        dto.setSecondaryColor(school.getSecondaryColor());
        dto.setNotes(school.getNotes());
        
        return dto;
    }
}



