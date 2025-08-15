package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.SchoolConfigDTO;
import com.devtech.school_management_system.entity.School;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SchoolService {
    boolean isSchoolConfigured();
    School getSchoolConfiguration();
    School setupSchool(SchoolConfigDTO schoolConfigDTO, MultipartFile logo, MultipartFile background) throws IOException;
    School updateSchool(Long id, SchoolConfigDTO schoolConfigDTO, MultipartFile logo, MultipartFile background) throws IOException;
}
