package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.ClassGroupWithStudentsDTO;
import com.devtech.school_management_system.dto.TeacherSubjectClassDTO;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.service.TeacherService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TeacherDashboardController {

    private final TeacherService teacherService;

    public TeacherDashboardController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/api/teachers/supervised-classes")
    @PreAuthorize("hasRole('TEACHER')")
    public List<ClassGroupWithStudentsDTO> getSupervisedClasses(Authentication authentication) {
        String username = authentication.getName();
        return teacherService.getSupervisedClasses(username);
    }

    @GetMapping("/api/teachers/assignments/current")
    @PreAuthorize("hasRole('TEACHER')")
    public List<TeacherSubjectClassDTO> getAssignedSubjectsAndClasses(Authentication authentication) {
        String username = authentication.getName();
        return teacherService.getAssignedSubjectsAndClassesDTO(username);
    }
}