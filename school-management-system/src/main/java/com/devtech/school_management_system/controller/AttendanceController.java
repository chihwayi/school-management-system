package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.entity.Attendance;
import com.devtech.school_management_system.service.AttendanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/attendance", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public Attendance markAttendance(@RequestParam Long studentId,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                     @RequestParam boolean present) {
        return attendanceService.markAttendance(studentId, date, present);
    }

    @GetMapping("/student/{studentId}")
    public List<Attendance> getAttendanceByStudent(@PathVariable Long studentId) {
        return attendanceService.getAttendanceByStudent(studentId);
    }

    @GetMapping("/date/{date}")
    public List<Attendance> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // Validate that the date is not in the future
        if (date.isAfter(LocalDate.now())) {
            return List.of(); // Return empty list for future dates
        }
        return attendanceService.getAttendanceByDate(date);
    }
}

