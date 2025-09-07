package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.StudentReportDTO;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.entity.Subject;
import com.devtech.school_management_system.repository.StudentRepository;
import com.devtech.school_management_system.service.ReportService;
import com.devtech.school_management_system.service.StudentFinanceService;
import com.devtech.school_management_system.service.SubjectService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/student", produces = MediaType.APPLICATION_JSON_VALUE)
public class StudentController {
    
    private final StudentRepository studentRepository;
    private final ReportService reportService;
    private final StudentFinanceService studentFinanceService;
    private final SubjectService subjectService;

    public StudentController(StudentRepository studentRepository, 
                           ReportService reportService,
                           StudentFinanceService studentFinanceService,
                           SubjectService subjectService) {
        this.studentRepository = studentRepository;
        this.reportService = reportService;
        this.studentFinanceService = studentFinanceService;
        this.subjectService = subjectService;
    }

    @GetMapping("/all")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/profile")
    public Student getStudentProfile(@RequestParam String mobileNumber) {
        return studentRepository.findByWhatsappNumber(mobileNumber)
            .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @GetMapping("/reports")
    public List<StudentReportDTO> getStudentReports(@RequestParam String mobileNumber) {
        Student student = studentRepository.findByWhatsappNumber(mobileNumber)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        return reportService.getReportsByStudent(student.getId());
    }

    @GetMapping("/reports/{studentId}")
    public List<StudentReportDTO> getStudentReportsById(@PathVariable Long studentId) {
        return reportService.getReportsByStudent(studentId);
    }

    @GetMapping("/finance")
    public Object getStudentFinance(@RequestParam String mobileNumber) {
        Student student = studentRepository.findByWhatsappNumber(mobileNumber)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        return studentFinanceService.getStudentFinanceStatus(student.getId());
    }

    @GetMapping("/finance/{studentId}")
    public Object getStudentFinanceById(@PathVariable Long studentId) {
        return studentFinanceService.getStudentFinanceStatus(studentId);
    }

    @GetMapping("/assignments")
    public List<Object> getStudentAssignments(@RequestParam String mobileNumber) {
        // TODO: Implement student assignments
        return List.of();
    }

    @GetMapping("/subjects")
    public List<Map<String, Object>> getStudentSubjects(@RequestParam String mobileNumber) {
        Student student = studentRepository.findByWhatsappNumber(mobileNumber)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Get subjects assigned to the student
        List<Subject> subjects = subjectService.getSubjectsByStudentId(student.getId());
        
        return subjects.stream()
            .map(subject -> {
                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("id", subject.getId());
                subjectData.put("name", subject.getName());
                subjectData.put("code", subject.getCode());
                subjectData.put("description", subject.getDescription());
                subjectData.put("level", subject.getLevel());
                subjectData.put("category", subject.getCategory());
                return subjectData;
            })
            .collect(Collectors.toList());
    }
}