package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.StudentReportDTO;
import com.devtech.school_management_system.entity.Attendance;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.service.AttendanceService;
import com.devtech.school_management_system.service.ParentService;
import com.devtech.school_management_system.service.ReportService;
import com.devtech.school_management_system.service.StudentFinanceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/parent", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParentController {
    
    private final ParentService parentService;
    private final ReportService reportService;
    private final AttendanceService attendanceService;
    private final StudentFinanceService studentFinanceService;

    public ParentController(ParentService parentService, 
                          ReportService reportService,
                          AttendanceService attendanceService,
                          StudentFinanceService studentFinanceService) {
        this.parentService = parentService;
        this.reportService = reportService;
        this.attendanceService = attendanceService;
        this.studentFinanceService = studentFinanceService;
    }

    // Get all reports for a student (only if fees are paid)
    @GetMapping("/reports/student/{studentId}")
    public ResponseEntity<List<StudentReportDTO>> getStudentReports(@PathVariable Long studentId) {
        // Check if fees are paid
        if (!studentFinanceService.isFeesPaid(studentId)) {
            return ResponseEntity.ok(List.of()); // Return empty list if fees not paid
        }
        
        List<StudentReportDTO> reports = reportService.getReportsByStudent(studentId);
        return ResponseEntity.ok(reports);
    }

    // Get a specific report with full details
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<StudentReportDTO> getReportDetails(@PathVariable Long reportId) {
        StudentReportDTO report = reportService.getReportById(reportId);
        
        // Check if fees are paid for this student
        if (!studentFinanceService.isFeesPaid(report.getStudentId())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(report);
    }

    // Get report PDF URL for viewing
    @GetMapping("/reports/{reportId}/pdf")
    public ResponseEntity<Map<String, String>> getReportPdf(@PathVariable Long reportId) {
        StudentReportDTO report = reportService.getReportById(reportId);
        
        // Check if fees are paid for this student
        if (!studentFinanceService.isFeesPaid(report.getStudentId())) {
            return ResponseEntity.notFound().build();
        }
        
        // Generate PDF URL (this would typically generate a signed URL or return a file path)
        String pdfUrl = reportService.generateReportPdf(reportId);
        
        Map<String, String> response = new HashMap<>();
        response.put("pdfUrl", pdfUrl);
        return ResponseEntity.ok(response);
    }

    // Get attendance summary for a student
    @GetMapping("/attendance/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentAttendanceSummary(@PathVariable Long studentId) {
        // Check if fees are paid
        if (!studentFinanceService.isFeesPaid(studentId)) {
            return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "studentName", "N/A",
                "totalDays", 0,
                "presentDays", 0,
                "absentDays", 0,
                "attendancePercentage", 0.0,
                "monthlyBreakdown", List.of(),
                "recentAttendance", List.of()
            ));
        }

        Student student = parentService.getStudentById(studentId);
        List<Attendance> attendanceRecords = attendanceService.getAttendanceByStudent(studentId);
        
        // Calculate summary statistics
        int totalDays = attendanceRecords.size();
        int presentDays = (int) attendanceRecords.stream().filter(Attendance::isPresent).count();
        int absentDays = totalDays - presentDays;
        double attendancePercentage = totalDays > 0 ? (double) presentDays / totalDays * 100 : 0.0;
        
        // Get monthly breakdown
        List<Map<String, Object>> monthlyBreakdown = getMonthlyBreakdown(attendanceRecords);
        
        // Get recent attendance (last 30 days)
        List<Map<String, Object>> recentAttendance = getRecentAttendance(attendanceRecords, 30);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("studentId", studentId);
        summary.put("studentName", student.getFirstName() + " " + student.getLastName());
        summary.put("totalDays", totalDays);
        summary.put("presentDays", presentDays);
        summary.put("absentDays", absentDays);
        summary.put("attendancePercentage", attendancePercentage);
        summary.put("monthlyBreakdown", monthlyBreakdown);
        summary.put("recentAttendance", recentAttendance);
        
        return ResponseEntity.ok(summary);
    }

    // Get monthly attendance breakdown
    @GetMapping("/attendance/student/{studentId}/monthly/{year}")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyAttendance(
            @PathVariable Long studentId, 
            @PathVariable int year) {
        
        // Check if fees are paid
        if (!studentFinanceService.isFeesPaid(studentId)) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Attendance> attendanceRecords = attendanceService.getAttendanceByStudent(studentId);
        List<Map<String, Object>> monthlyBreakdown = getMonthlyBreakdown(attendanceRecords, year);
        
        return ResponseEntity.ok(monthlyBreakdown);
    }

    // Get detailed attendance records for a student
    @GetMapping("/attendance/student/{studentId}/records")
    public ResponseEntity<List<Map<String, Object>>> getStudentAttendanceRecords(@PathVariable Long studentId) {
        // Check if fees are paid
        if (!studentFinanceService.isFeesPaid(studentId)) {
            return ResponseEntity.ok(List.of());
        }

        List<Attendance> attendanceRecords = attendanceService.getAttendanceByStudent(studentId);
        
        List<Map<String, Object>> records = attendanceRecords.stream()
            .map(record -> {
                Map<String, Object> recordData = new HashMap<>();
                recordData.put("id", record.getId());
                recordData.put("date", record.getDate().toString());
                recordData.put("status", record.isPresent() ? "PRESENT" : "ABSENT");
                recordData.put("subject", null); // Subject field not available in Attendance entity
                recordData.put("remarks", null); // Remarks field not available in Attendance entity
                return recordData;
            })
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(records);
    }

    // Get recent attendance records
    @GetMapping("/attendance/student/{studentId}/recent/{days}")
    public ResponseEntity<List<Map<String, Object>>> getRecentAttendance(
            @PathVariable Long studentId, 
            @PathVariable int days) {
        
        // Check if fees are paid
        if (!studentFinanceService.isFeesPaid(studentId)) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Attendance> attendanceRecords = attendanceService.getAttendanceByStudent(studentId);
        List<Map<String, Object>> recentAttendance = getRecentAttendance(attendanceRecords, days);
        
        return ResponseEntity.ok(recentAttendance);
    }

    // Check if student fees are fully paid
    @GetMapping("/finance/student/{studentId}")
    public ResponseEntity<Map<String, Object>> checkFeesStatus(@PathVariable Long studentId) {
        Map<String, Object> financeStatus = studentFinanceService.getStudentFinanceStatus(studentId);
        return ResponseEntity.ok(financeStatus);
    }

    // Helper method to get monthly breakdown
    private List<Map<String, Object>> getMonthlyBreakdown(List<Attendance> attendanceRecords) {
        return getMonthlyBreakdown(attendanceRecords, LocalDate.now().getYear());
    }

    private List<Map<String, Object>> getMonthlyBreakdown(List<Attendance> attendanceRecords, int year) {
        Map<Integer, Map<String, Object>> monthlyData = new HashMap<>();
        
        for (Attendance attendance : attendanceRecords) {
            if (attendance.getDate().getYear() == year) {
                int month = attendance.getDate().getMonthValue();
                
                monthlyData.computeIfAbsent(month, k -> {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("month", String.valueOf(k));
                    monthData.put("year", year);
                    monthData.put("totalDays", 0);
                    monthData.put("presentDays", 0);
                    monthData.put("absentDays", 0);
                    monthData.put("percentage", 0.0);
                    return monthData;
                });
                
                Map<String, Object> monthData = monthlyData.get(month);
                monthData.put("totalDays", (Integer) monthData.get("totalDays") + 1);
                
                if (attendance.isPresent()) {
                    monthData.put("presentDays", (Integer) monthData.get("presentDays") + 1);
                } else {
                    monthData.put("absentDays", (Integer) monthData.get("absentDays") + 1);
                }
                
                int totalDays = (Integer) monthData.get("totalDays");
                int presentDays = (Integer) monthData.get("presentDays");
                double percentage = totalDays > 0 ? (double) presentDays / totalDays * 100 : 0.0;
                monthData.put("percentage", percentage);
            }
        }
        
        return monthlyData.values().stream()
                .sorted((a, b) -> Integer.compare(
                    Integer.parseInt((String) a.get("month")), 
                    Integer.parseInt((String) b.get("month"))
                ))
                .toList();
    }

    // Helper method to get recent attendance
    private List<Map<String, Object>> getRecentAttendance(List<Attendance> attendanceRecords, int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        
        return attendanceRecords.stream()
                .filter(attendance -> attendance.getDate().isAfter(cutoffDate))
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .map(attendance -> {
                    Map<String, Object> record = new HashMap<>();
                    record.put("date", attendance.getDate().toString());
                    record.put("present", attendance.isPresent());
                    record.put("markedBy", attendance.getMarkedBy());
                    return record;
                })
                .toList();
    }
}
