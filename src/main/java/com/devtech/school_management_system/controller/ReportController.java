package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.OverallCommentDTO;
import com.devtech.school_management_system.dto.SubjectCommentDTO;
import com.devtech.school_management_system.entity.Report;
import com.devtech.school_management_system.entity.SubjectReport;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.service.ReportService;
import com.devtech.school_management_system.service.TeacherService;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
    private final ReportService reportService;
    private final TeacherService teacherService;

    public ReportController(ReportService reportService, TeacherService teacherService) {
        this.reportService = reportService;
        this.teacherService = teacherService;
    }

    @PostMapping("/generate/class/{classGroupId}/term/{term}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public List<Report> generateClassReports(@PathVariable Long classGroupId,
                                             @PathVariable String term,
                                             @PathVariable String year) {
        return reportService.generateClassReports(classGroupId, term, year);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Report> getStudentReports(@PathVariable Long studentId) {
        return reportService.getStudentReports(studentId);
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public Report getReportById(@PathVariable Long reportId) {
        return reportService.getReportById(reportId);
    }

    @PostMapping("/{reportId}/subject-comment")
    @PreAuthorize("hasRole('TEACHER')")
    public SubjectReport addSubjectComment(@PathVariable Long reportId,
                                           @RequestBody SubjectCommentDTO commentDTO,
                                           Authentication authentication) {
        String username = authentication.getName();
        Teacher teacher = teacherService.getTeacherByUsername(username);

        // Verify teacher is authorized to comment on this subject report
        if (!teacherService.canTeacherCommentOnSubject(teacher.getId(),
                reportId,
                commentDTO.getSubjectId())) {
            throw new AccessDeniedException("You are not authorized to comment on this subject report");
        }

        return reportService.addSubjectComment(reportId,
                commentDTO.getSubjectId(),
                commentDTO.getComment(),
                teacher.getId());
    }

    @PostMapping("/{reportId}/overall-comment")
    @PreAuthorize("hasRole('TEACHER')")
    public Report addOverallComment(@PathVariable Long reportId,
                                    @RequestBody OverallCommentDTO commentDTO,
                                    Authentication authentication) {
        String username = authentication.getName();
        Teacher teacher = teacherService.getTeacherByUsername(username);

        // Verify teacher is the class teacher for this report
        if (!reportService.isClassTeacherForReport(teacher.getId(), reportId)) {
            throw new AccessDeniedException("Only the class teacher can add overall comments to this report");
        }

        return reportService.addOverallComment(reportId, commentDTO.getComment(), teacher.getId());
    }

    @PostMapping("/{reportId}/finalize")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public Report finalizeReport(@PathVariable Long reportId) {
        return reportService.finalizeReport(reportId);
    }

    @GetMapping("/class/{classGroupId}/term/{term}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK', 'TEACHER')")
    public List<Report> getClassReports(@PathVariable Long classGroupId,
                                        @PathVariable String term,
                                        @PathVariable String year) {
        return reportService.getClassReports(classGroupId, term, year);
    }
}

