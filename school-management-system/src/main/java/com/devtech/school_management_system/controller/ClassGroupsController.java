package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.ClassGroupDTO;
import com.devtech.school_management_system.entity.ClassGroup;
import com.devtech.school_management_system.service.ClassGroupService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/class-groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClassGroupsController {
    private final ClassGroupService classGroupService;

    public ClassGroupsController(ClassGroupService classGroupService) {
        this.classGroupService = classGroupService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<ClassGroupDTO> getAllClassGroups() {
        return classGroupService.getAllClassGroupsDTO();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public ClassGroupDTO getClassGroupById(@PathVariable Long id) {
        ClassGroup classGroup = classGroupService.getClassGroupById(id);
        return classGroupService.convertToDTO(classGroup);
    }
}