package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.SchoolConfigDTO;
import com.devtech.school_management_system.entity.School;
import com.devtech.school_management_system.exception.ResourceNotFoundException;
import com.devtech.school_management_system.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Value("${file.upload.directory:./uploads}")
    private String uploadDirectory;

    @Override
    public boolean isSchoolConfigured() {
        return schoolRepository.findAll().stream().anyMatch(School::isConfigured);
    }
    @Override
    public School getSchoolConfiguration() {
        return schoolRepository.findAll().stream().findFirst().orElse(null);
    }

    @Override
    public School setupSchool(SchoolConfigDTO schoolConfigDTO, MultipartFile logo, MultipartFile background) throws IOException {
        return setupSchool(schoolConfigDTO, logo, background, null);
    }

    public School setupSchool(SchoolConfigDTO schoolConfigDTO, MultipartFile logo, MultipartFile background, MultipartFile ministryLogo) throws IOException {
        if (isSchoolConfigured()) {
            throw new IllegalStateException("School is already configured");
        }

        School school = new School();
        mapDTOToEntity(schoolConfigDTO, school);

        if (logo != null && !logo.isEmpty()) {
            String logoPath = saveFile(logo, "logo");
            school.setLogoPath(logoPath);
        }

        if (background != null && !background.isEmpty()) {
            String backgroundPath = saveFile(background, "background");
            school.setBackgroundPath(backgroundPath);
        }

        if (ministryLogo != null && !ministryLogo.isEmpty()) {
            String ministryLogoPath = saveFile(ministryLogo, "ministry_logo");
            school.setMinistryLogoPath(ministryLogoPath);
        }

        return schoolRepository.save(school);
    }

    @Override
    public School updateSchool(Long id, SchoolConfigDTO schoolConfigDTO, MultipartFile logo, MultipartFile background) throws IOException {
        return updateSchool(id, schoolConfigDTO, logo, background, null);
    }

    public School updateSchool(Long id, SchoolConfigDTO schoolConfigDTO, MultipartFile logo, MultipartFile background, MultipartFile ministryLogo) throws IOException {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));

        mapDTOToEntity(schoolConfigDTO, school);

        if (logo != null && !logo.isEmpty()) {
            // Delete old logo if exists
            if (school.getLogoPath() != null) {
                deleteFile(school.getLogoPath());
            }
            String logoPath = saveFile(logo, "logo");
            school.setLogoPath(logoPath);
        }

        if (background != null && !background.isEmpty()) {
            // Delete old background if exists
            if (school.getBackgroundPath() != null) {
                deleteFile(school.getBackgroundPath());
            }
            String backgroundPath = saveFile(background, "background");
            school.setBackgroundPath(backgroundPath);
        }

        if (ministryLogo != null && !ministryLogo.isEmpty()) {
            // Delete old ministry logo if exists
            if (school.getMinistryLogoPath() != null) {
                deleteFile(school.getMinistryLogoPath());
            }
            String ministryLogoPath = saveFile(ministryLogo, "ministry_logo");
            school.setMinistryLogoPath(ministryLogoPath);
        }

        return schoolRepository.save(school);
    }

    private void mapDTOToEntity(SchoolConfigDTO dto, School school) {
        school.setName(dto.getName());
        school.setDescription(dto.getDescription());
        school.setPrimaryColor(dto.getPrimaryColor());
        school.setSecondaryColor(dto.getSecondaryColor());
        school.setContactEmail(dto.getContactEmail());
        school.setContactPhone(dto.getContactPhone());
        school.setAddress(dto.getAddress());
        school.setWebsite(dto.getWebsite());
        school.setConfigured(true);
        school.setActive(true);
    }

    private String saveFile(MultipartFile file, String prefix) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFileName = prefix + "_" + UUID.randomUUID().toString() + "_" + fileName;

        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/api/uploads/" + uniqueFileName;
    }

    private void deleteFile(String filePath) {
        try {
            if (filePath != null && filePath.startsWith("/api/uploads/")) {
                String fileName = filePath.substring("/api/uploads/".length());
                Path path = Paths.get(uploadDirectory, fileName);
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            // Ignore file deletion errors
        }
    }
}
