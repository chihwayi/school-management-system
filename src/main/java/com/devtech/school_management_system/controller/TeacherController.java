package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.TeacherRegistrationDTO;
import com.devtech.school_management_system.entity.ClassGroup;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.entity.TeacherSubjectClass;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.service.TeacherService;
import com.devtech.school_management_system.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/teachers", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeacherController {
    private final TeacherService teacherService;
    private final UserService userService;

    public TeacherController(TeacherService teacherService, UserService userService) {
        this.teacherService = teacherService;
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public Teacher getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Teacher createTeacher(@RequestBody TeacherRegistrationDTO registrationDTO) {
        // Create user account first
        User user = userService.createTeacherUser(registrationDTO.getUsername(),
                registrationDTO.getEmail(),
                registrationDTO.getPassword());

        // Then create teacher profile
        Teacher teacher = new Teacher();
        teacher.setFirstName(registrationDTO.getFirstName());
        teacher.setLastName(registrationDTO.getLastName());
        teacher.setEmployeeId(registrationDTO.getEmployeeId());
        teacher.setUser(user);

        return teacherService.createTeacher(teacher);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Teacher updateTeacher(@PathVariable Long id, @RequestBody Teacher teacherDetails) {
        return teacherService.updateTeacher(id, teacherDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('TEACHER')")
    public Teacher getCurrentTeacher(Authentication authentication) {
        String username = authentication.getName();
        return teacherService.getTeacherByUsername(username);
    }

    @GetMapping("/subjects/assigned")
    @PreAuthorize("hasRole('TEACHER')")
    public List<TeacherSubjectClass> getAssignedSubjectsAndClasses(Authentication authentication) {
        String username = authentication.getName();
        return teacherService.getAssignedSubjectsAndClasses(username);
    }

    @GetMapping("/class-teacher-assignments")
    @PreAuthorize("hasRole('TEACHER')")
    public List<ClassGroup> getSupervisedClasses(Authentication authentication) {
        String username = authentication.getName();
        return teacherService.getSupervisedClasses(username);
    }
}

