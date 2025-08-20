package com.devtech.school_management_system.dto;

import java.time.LocalDate;

public class AttendanceRequestDTO {
    private Long studentId;
    private LocalDate date;
    private boolean present;

    public AttendanceRequestDTO() {
    }

    public AttendanceRequestDTO(Long studentId, LocalDate date, boolean present) {
        this.studentId = studentId;
        this.date = date;
        this.present = present;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
