package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.TeacherAssignmentDTO;
import com.devtech.school_management_system.entity.TeacherSubjectClass;
import com.devtech.school_management_system.service.TeacherAssignmentService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/teacher-assignments", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeacherAssignmentController {
    private final TeacherAssignmentService teacherAssignmentService;

    public TeacherAssignmentController(TeacherAssignmentService teacherAssignmentService) {
        this.teacherAssignmentService = teacherAssignmentService;
    }

    @PostMapping
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public void removeTeacherAssignment(@PathVariable Long id) {
        teacherAssignmentService.removeTeacherAssignment(id);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<TeacherSubjectClass> getTeacherAssignments(@PathVariable Long teacherId) {
        return teacherAssignmentService.getTeacherAssignments(teacherId);
    }

    @GetMapping("/subject/{subjectId}/form/{form}/section/{section}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public TeacherSubjectClass getAssignmentForClass(
            @PathVariable Long subjectId,
            @PathVariable String form,
            @PathVariable String section,
            @PathVariable String year) {
        return teacherAssignmentService.getAssignmentForClass(subjectId, form, section, year);
    }
}

