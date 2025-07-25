package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.StudentReportDTO;
import com.devtech.school_management_system.dto.SubjectCommentDTO;
import com.devtech.school_management_system.dto.OverallCommentDTO;
import com.devtech.school_management_system.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}