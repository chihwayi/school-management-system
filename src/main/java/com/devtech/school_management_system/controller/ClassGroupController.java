package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.ClassGroup;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.service.ClassGroupService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/classes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClassGroupController {
    private final ClassGroupService classGroupService;

    public ClassGroupController(ClassGroupService classGroupService) {
        this.classGroupService = classGroupService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<ClassGroup> getAllClassGroups() {
        return classGroupService.getAllClassGroups();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public ClassGroup getClassGroupById(@PathVariable Long id) {
        return classGroupService.getClassGroupById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ClassGroup createClassGroup(@RequestBody ClassGroup classGroup) {
        return classGroupService.createClassGroup(classGroup);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ClassGroup updateClassGroup(@PathVariable Long id, @RequestBody ClassGroup classGroupDetails) {
        return classGroupService.updateClassGroup(id, classGroupDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteClassGroup(@PathVariable Long id) {
        classGroupService.deleteClassGroup(id);
    }

    @GetMapping("/form/{form}/section/{section}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public ClassGroup getClassGroupByDetails(@PathVariable String form,
                                             @PathVariable String section,
                                             @PathVariable String year) {
        return classGroupService.getClassGroupByDetails(form, section, year);
    }

    @GetMapping("/{classGroupId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Student> getStudentsInClass(@PathVariable Long classGroupId) {
        return classGroupService.getStudentsInClass(classGroupId);
    }

    @PostMapping("/{classGroupId}/assign-teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ClassGroup assignClassTeacher(@PathVariable Long classGroupId, @PathVariable Long teacherId) {
        return classGroupService.assignClassTeacher(classGroupId, teacherId);
    }
}

