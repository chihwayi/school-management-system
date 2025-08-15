package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.AssessmentDTO;
import com.devtech.school_management_system.dto.AssessmentResponseDTO;
import com.devtech.school_management_system.dto.AssessmentUpdateDTO;
import com.devtech.school_management_system.entity.Assessment;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.service.AssessmentService;
import com.devtech.school_management_system.service.TeacherService;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/assessments", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssessmentController {
    private final AssessmentService assessmentService;
    private final TeacherService teacherService;

    public AssessmentController(AssessmentService assessmentService, TeacherService teacherService) {
        this.assessmentService = assessmentService;
        this.teacherService = teacherService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER')")
    public AssessmentResponseDTO recordAssessment(@RequestBody AssessmentDTO assessmentDTO, Authentication authentication) {
        String username = authentication.getName();
        Teacher teacher = teacherService.getTeacherByUsername(username);

        return assessmentService.recordAssessmentByStudentAndSubject(
                assessmentDTO.getStudentId(),
                assessmentDTO.getSubjectId(),
                assessmentDTO.getTitle(),
                assessmentDTO.getDate(),
                assessmentDTO.getScore(),
                assessmentDTO.getMaxScore(),
                assessmentDTO.getType(),
                assessmentDTO.getTerm(),
                assessmentDTO.getAcademicYear()
        );
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLERK', 'ROLE_TEACHER', 'ROLE_CLASS_TEACHER')")
    public List<com.devtech.school_management_system.dto.AssessmentResponseDTO> getStudentSubjectAssessments(@PathVariable Long studentId,
                                                         @PathVariable Long subjectId) {
        return assessmentService.getStudentSubjectAssessments(studentId, subjectId);
    }

    @GetMapping("/student/{studentId}/term/{term}/year/{year}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLERK', 'ROLE_TEACHER', 'ROLE_CLASS_TEACHER')")
    public List<com.devtech.school_management_system.dto.AssessmentResponseDTO> getStudentTermAssessments(@PathVariable Long studentId,
                                                      @PathVariable String term,
                                                      @PathVariable String year) {
        return assessmentService.getStudentTermAssessments(studentId, term, year);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER')")
    public AssessmentResponseDTO updateAssessment(@PathVariable Long id,
                                       @RequestBody AssessmentUpdateDTO updateDTO,
                                       Authentication authentication) {
        String username = authentication.getName();
        Teacher teacher = teacherService.getTeacherByUsername(username);

        // Verify teacher is authorized to update this assessment
        if (!assessmentService.canTeacherUpdateAssessment(teacher.getId(), id)) {
            throw new AccessDeniedException("You are not authorized to update this assessment");
        }

        return assessmentService.updateAssessment(
                id,
                updateDTO.getTitle(),
                updateDTO.getDate(),
                updateDTO.getScore(),
                updateDTO.getMaxScore()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_CLASS_TEACHER')")
    public Assessment getAssessmentById(@PathVariable Long id) {
        return assessmentService.getAssessmentById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_CLASS_TEACHER')")
    public void deleteAssessment(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        Teacher teacher = teacherService.getTeacherByUsername(username);

        // Only allow teacher to delete if they are authorized for this assessment
        if (!assessmentService.canTeacherUpdateAssessment(teacher.getId(), id) &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You are not authorized to delete this assessment");
        }

        assessmentService.deleteAssessment(id);
    }
}

