package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.ClassGroupDTO;
import com.devtech.school_management_system.entity.ClassGroup;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.service.ClassGroupService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/classes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClassGroupController {
    private final ClassGroupService classGroupService;
    private final StudentRepository studentRepository;

    public ClassGroupController(ClassGroupService classGroupService, StudentRepository studentRepository) {
        this.classGroupService = classGroupService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<ClassGroupDTO> getAllClassGroups() {
        return classGroupService.getAllClassGroupsDTO();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public ClassGroupDTO getClassGroupById(@PathVariable Long id) {
        ClassGroup classGroup = classGroupService.getClassGroupById(id);
        return classGroupService.convertToDTO(classGroup);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ClassGroupDTO createClassGroup(@RequestBody ClassGroupDTO classGroupDTO) {
        try {
            // Convert DTO to entity
            ClassGroup classGroup = new ClassGroup();
            classGroup.setForm(classGroupDTO.getForm());
            classGroup.setSection(classGroupDTO.getSection());
            classGroup.setAcademicYear(classGroupDTO.getAcademicYear());
            classGroup.setLevel(classGroupDTO.getLevel());
            classGroup.setClassCapacity(classGroupDTO.getClassCapacity());
            
            ClassGroup createdClassGroup = classGroupService.createClassGroup(classGroup);
            
            // If a teacher ID is provided, assign the teacher
            if (classGroupDTO.getClassTeacherId() != null) {
                createdClassGroup = classGroupService.assignClassTeacher(createdClassGroup.getId(), classGroupDTO.getClassTeacherId());
            }
            
            return classGroupService.convertToDTO(createdClassGroup);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ClassGroupDTO updateClassGroup(@PathVariable Long id, @RequestBody ClassGroupDTO classGroupDTO) {
        try {
            // First get the existing class group
            ClassGroup existingClassGroup = classGroupService.getClassGroupById(id);
            
            // Update basic properties
            existingClassGroup.setForm(classGroupDTO.getForm());
            existingClassGroup.setSection(classGroupDTO.getSection());
            existingClassGroup.setAcademicYear(classGroupDTO.getAcademicYear());
            existingClassGroup.setLevel(classGroupDTO.getLevel());
            existingClassGroup.setClassCapacity(classGroupDTO.getClassCapacity());
            
            // Update the class group
            ClassGroup updatedClassGroup = classGroupService.updateClassGroup(id, existingClassGroup);
            
            // If a teacher ID is provided, assign the teacher
            if (classGroupDTO.getClassTeacherId() != null) {
                updatedClassGroup = classGroupService.assignClassTeacher(id, classGroupDTO.getClassTeacherId());
            }
            
            return classGroupService.convertToDTO(updatedClassGroup);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteClassGroup(@PathVariable Long id) {
        classGroupService.deleteClassGroup(id);
    }

    @GetMapping("/form/{form}/section/{section}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public ClassGroupDTO getClassGroupByDetails(@PathVariable String form,
                                             @PathVariable String section,
                                             @PathVariable String year) {
        ClassGroup classGroup = classGroupService.getClassGroupByDetails(form, section, year);
        return classGroupService.convertToDTO(classGroup);
    }

    @GetMapping("/{classGroupId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Student> getStudentsInClass(@PathVariable Long classGroupId) {
        return classGroupService.getStudentsInClass(classGroupId);
    }

    @PostMapping("/{classGroupId}/assign-teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ClassGroupDTO assignClassTeacher(@PathVariable Long classGroupId, @PathVariable Long teacherId) {
        ClassGroup classGroup = classGroupService.assignClassTeacher(classGroupId, teacherId);
        return classGroupService.convertToDTO(classGroup);
    }
}

