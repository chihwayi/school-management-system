package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.ClassGroupWithStudentsDTO;
import com.devtech.school_management_system.dto.TeacherAssignmentDTO;
import com.devtech.school_management_system.dto.TeacherRegistrationDTO;
import com.devtech.school_management_system.dto.TeacherSubjectClassDTO;
import com.devtech.school_management_system.dto.TeacherWithUserDTO;
import com.devtech.school_management_system.entity.ClassGroup;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.entity.TeacherSubjectClass;
import com.devtech.school_management_system.entity.User;
import com.devtech.school_management_system.service.TeacherAssignmentService;
import com.devtech.school_management_system.service.TeacherService;
import com.devtech.school_management_system.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/teachers", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeacherController {
    private final TeacherService teacherService;
    private final UserService userService;
    private final TeacherAssignmentService teacherAssignmentService;

    public TeacherController(TeacherService teacherService, UserService userService, TeacherAssignmentService teacherAssignmentService) {
        this.teacherService = teacherService;
        this.userService = userService;
        this.teacherAssignmentService = teacherAssignmentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Object getAllTeachers(@RequestParam(required = false) Boolean includeUser) {
        if (Boolean.TRUE.equals(includeUser)) {
            return teacherService.getAllTeachersWithUserDetails();
        }
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
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER')")
    public Teacher getCurrentTeacher(Authentication authentication) {
        String username = authentication.getName();
        return teacherService.getTeacherByUsername(username);
    }

    @GetMapping("/assignments/current")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER')")
    public List<TeacherSubjectClassDTO> getAssignedSubjectsAndClasses(Authentication authentication) {
        String username = authentication.getName();
        return teacherService.getAssignedSubjectsAndClassesDTO(username);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Object getAllTeachersWithDetails(@RequestParam(required = false) Boolean includeUser) {
        if (Boolean.TRUE.equals(includeUser)) {
            return teacherService.getAllTeachersWithUserDetails();
        }
        return teacherService.getAllTeachers();
    }

    @GetMapping("/supervised-classes")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER')")
    public List<ClassGroupWithStudentsDTO> getSupervisedClasses(Authentication authentication) {
        String username = authentication.getName();
        return teacherService.getSupervisedClasses(username);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public TeacherSubjectClass assignTeacherToSubjectAndClass(@RequestBody TeacherAssignmentDTO assignmentDTO) {
        return teacherAssignmentService.assignTeacherToSubjectAndClass(
                assignmentDTO.getTeacherId(),
                assignmentDTO.getSubjectId(),
                assignmentDTO.getForm(),
                assignmentDTO.getSection(),
                assignmentDTO.getAcademicYear()
        );
    }
    
    @GetMapping("/{teacherId}/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<TeacherSubjectClassDTO> getTeacherAssignments(@PathVariable Long teacherId) {
        return teacherAssignmentService.getTeacherAssignmentsDTO(teacherId);
    }
    
    @PostMapping("/{teacherId}/bulk-assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    @Transactional
    public List<TeacherSubjectClass> saveTeacherAssignments(
            @PathVariable Long teacherId,
            @RequestBody Map<String, List<Map<String, Object>>> requestBody) {
        
        System.out.println("Received bulk assignment request for teacher: " + teacherId);
        List<Map<String, Object>> assignments = requestBody.get("assignments");
        System.out.println("Number of assignments received: " + (assignments != null ? assignments.size() : 0));
        
        if (assignments == null || assignments.isEmpty()) {
            System.out.println("No assignments provided, returning empty list");
            return teacherAssignmentService.getTeacherAssignments(teacherId);
        }
        
        List<TeacherSubjectClass> result = teacherAssignmentService.saveBulkAssignments(teacherId, assignments);
        System.out.println("Returning " + result.size() + " assignments after bulk save");
        return result;
    }
}