package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.StudentReportDTO;
import com.devtech.school_management_system.dto.SubjectCommentDTO;
import com.devtech.school_management_system.dto.OverallCommentDTO;
import com.devtech.school_management_system.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.devtech.school_management_system.dto.ApiResponse;

@RestController
@RequestMapping(value = "/api/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
    
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/class/{form}/{section}/{term}/{year}")
    @PreAuthorize("hasAnyRole('ROLE_CLASS_TEACHER', 'ROLE_ADMIN')")
    public List<StudentReportDTO> getClassReports(@PathVariable String form,
                                                  @PathVariable String section,
                                                  @PathVariable String term,
                                                  @PathVariable String year,
                                                  Authentication authentication) {
        return reportService.getClassReports(form, section, term, year, authentication.getName());
    }

    @GetMapping("/subject/{subjectId}/{form}/{section}/{term}/{year}")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER')")
    public List<StudentReportDTO> getSubjectReports(@PathVariable Long subjectId,
                                                    @PathVariable String form,
                                                    @PathVariable String section,
                                                    @PathVariable String term,
                                                    @PathVariable String year,
                                                    Authentication authentication) {
        System.out.println("ReportController.getSubjectReports called with subjectId: " + subjectId + ", form: " + form + ", section: " + section + ", term: " + term + ", year: " + year + ", user: " + authentication.getName());
        return reportService.getSubjectReports(subjectId, form, section, term, year, authentication.getName());
    }

    @PostMapping("/subject-comment")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER')")
    public void addSubjectComment(@RequestBody SubjectCommentDTO commentDTO,
                                  Authentication authentication) {
        reportService.addSubjectComment(commentDTO, authentication.getName());
    }

    @PostMapping("/overall-comment")
    @PreAuthorize("hasRole('ROLE_CLASS_TEACHER')")
    public void addOverallComment(@RequestBody OverallCommentDTO commentDTO,
                                  Authentication authentication) {
        reportService.addOverallComment(commentDTO, authentication.getName());
    }

    @PostMapping("/{reportId}/finalize")
    @PreAuthorize("hasRole('ROLE_CLASS_TEACHER')")
    public void finalizeReport(@PathVariable Long reportId,
                                Authentication authentication) {
        reportService.finalizeReport(reportId, authentication.getName());
    }

    /**
     * Update attendance statistics for a specific report
     */
    @PostMapping("/{reportId}/attendance")
    @PreAuthorize("hasAnyRole('ROLE_CLASS_TEACHER', 'ROLE_ADMIN')")
    public void updateReportAttendance(@PathVariable Long reportId,
                                       @RequestParam String term,
                                       @RequestParam String academicYear) {
        reportService.updateReportAttendance(reportId, term, academicYear);
    }

    /**
     * Update attendance statistics for all reports in a class
     */
    @PostMapping("/class/{form}/{section}/attendance")
    @PreAuthorize("hasAnyRole('ROLE_CLASS_TEACHER', 'ROLE_ADMIN')")
    public void updateClassAttendance(@PathVariable String form,
                                      @PathVariable String section,
                                      @RequestParam String term,
                                      @RequestParam String academicYear) {
        reportService.updateClassReportsAttendance(form, section, term, academicYear);
    }

    /**
     * Get all reports for a specific student
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER', 'ROLE_ADMIN')")
    public List<StudentReportDTO> getStudentReports(@PathVariable Long studentId,
                                                    Authentication authentication) {
        return reportService.getStudentReports(studentId, authentication.getName());
    }

    /**
     * Get a specific report by ID
     */
    @GetMapping("/{reportId}")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_CLASS_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<StudentReportDTO> getReportById(@PathVariable Long reportId,
                                                          Authentication authentication) {
        StudentReportDTO report = reportService.getReportById(reportId, authentication.getName());
        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}