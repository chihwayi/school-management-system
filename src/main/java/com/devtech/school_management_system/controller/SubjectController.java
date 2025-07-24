package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.enums.SubjectCategory;
import com.devtech.school_management_system.service.SubjectService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubjectController {
    private final SubjectService subjectService;
    private final com.devtech.school_management_system.repository.StudentSubjectRepository studentSubjectRepository;

    public SubjectController(SubjectService subjectService,
                           com.devtech.school_management_system.repository.StudentSubjectRepository studentSubjectRepository) {
        this.subjectService = subjectService;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Subject> getSubjectsByCategory(@PathVariable SubjectCategory category) {
        return subjectService.getSubjectsByCategory(category);
    }

    @GetMapping("/level/{level}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Subject> getSubjectsByLevel(@PathVariable String level) {
        return subjectService.getSubjectsByLevel(level);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectService.createSubject(subject);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Subject updateSubject(@PathVariable Long id, @RequestBody Subject subject) {
        return subjectService.updateSubject(id, subject);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public Subject getSubjectById(@PathVariable Long id) {
        return subjectService.getSubjectById(id);
    }

    @GetMapping("/{id}/teachers")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<com.devtech.school_management_system.dto.TeacherSubjectAssignmentDTO> getTeachersBySubject(@PathVariable Long id) {
        return subjectService.getTeachersWithClassesBySubject(id);
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<com.devtech.school_management_system.dto.StudentDTO> getStudentsBySubject(@PathVariable Long id) {
        return subjectService.getStudentsBySubject(id);
    }

    @GetMapping("/{id}/classes")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<String> getClassesBySubject(@PathVariable Long id) {
        return subjectService.getClassesBySubject(id);
    }

    @GetMapping("/{id}/test")
    public String testSubjectData(@PathVariable Long id) {
        try {
            List<com.devtech.school_management_system.entity.StudentSubject> studentSubjects = 
                studentSubjectRepository.findBySubjectId(id);
            return "Found " + studentSubjects.size() + " student-subject records for subject " + id;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }
}

