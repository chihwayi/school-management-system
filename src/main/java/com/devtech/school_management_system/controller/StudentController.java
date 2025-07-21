package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.StudentSubject;
import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.dto.StudentRegistrationDTO;
import com.devtech.school_management_system.dto.StudentUpdateDTO;
import com.devtech.school_management_system.dto.PromotionToALevelDTO;
import com.devtech.school_management_system.service.StudentService;
import com.devtech.school_management_system.repository.StudentRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/students", produces = MediaType.APPLICATION_JSON_VALUE)
public class StudentController {
    private final StudentService studentService;
    private final StudentRepository studentRepository;

    public StudentController(StudentService studentService, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Student createStudent(@RequestBody StudentRegistrationDTO registrationDTO) {
        return studentService.createStudent(
                registrationDTO.getFirstName(),
                registrationDTO.getLastName(),
                registrationDTO.getStudentId(),
                registrationDTO.getForm(),
                registrationDTO.getSection(),
                registrationDTO.getLevel(),
                registrationDTO.getAcademicYear()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Student updateStudent(@PathVariable Long id, @RequestBody StudentUpdateDTO updateDTO) {
        return studentService.updateStudent(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping("/form/{form}/section/{section}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Student> getStudentsByClass(@PathVariable String form, @PathVariable String section) {
        return studentService.getStudentsByClass(form, section);
    }

    @PostMapping("/{id}/assign-subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public StudentSubject assignSubjectToStudent(@PathVariable Long id, @PathVariable Long subjectId) {
        return studentService.assignSubjectToStudent(id, subjectId);
    }

    @DeleteMapping("/{id}/remove-subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public void removeSubjectFromStudent(@PathVariable Long id, @PathVariable Long subjectId) {
        studentService.removeSubjectFromStudent(id, subjectId);
    }

    @GetMapping("/{id}/subjects")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Subject> getStudentSubjects(@PathVariable Long id) {
        return studentService.getStudentSubjects(id);
    }

    @PostMapping("/batch/advance-form")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<Student> advanceStudentsToNextForm(@RequestBody List<Long> studentIds) {
        return studentService.advanceStudentsToNextForm(studentIds);
    }

    @PostMapping("/batch/promote-to-a-level")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<Student> promoteStudentsToALevel(@RequestBody PromotionToALevelDTO promotionDTO) {
        return studentService.promoteStudentsToALevel(
                promotionDTO.getStudentIds(),
                promotionDTO.getSubjectIds(),
                promotionDTO.getForm(),
                promotionDTO.getSection()
        );
    }
    
    @PostMapping("/fix-academic-years")
    @PreAuthorize("hasRole('ADMIN')")
    public String fixAcademicYears() {
        List<Student> students = studentRepository.findAll();
        int updatedCount = 0;
        
        for (Student student : students) {
            if ("2024-2025".equals(student.getAcademicYear())) {
                student.setAcademicYear("2025");
                studentRepository.save(student);
                updatedCount++;
            }
        }
        
        return "Fixed academic years for " + updatedCount + " students";
    }
}

