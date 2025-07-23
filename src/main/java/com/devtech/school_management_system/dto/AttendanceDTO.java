package com.devtech.school_management_system.dto;

public class AttendanceDTO {
    private Integer attendanceDays;
    private Integer totalSchoolDays;

    public AttendanceDTO() {
    }

    public AttendanceDTO(Integer attendanceDays, Integer totalSchoolDays) {
        this.attendanceDays = attendanceDays;
        this.totalSchoolDays = totalSchoolDays;
    }

    public Integer getAttendanceDays() {
        return attendanceDays;
    }

    public void setAttendanceDays(Integer attendanceDays) {
        this.attendanceDays = attendanceDays;
    }

    public Integer getTotalSchoolDays() {
        return totalSchoolDays;
    }

    public void setTotalSchoolDays(Integer totalSchoolDays) {
        this.totalSchoolDays = totalSchoolDays;
    }
}