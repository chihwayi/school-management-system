package com.devtech.school_management_system.dto;

public class AttendanceStatistics {
    private int presentDays;
    private int absentDays;
    private int totalSchoolDays;
    private double attendancePercentage;

    public AttendanceStatistics() {
    }

    public AttendanceStatistics(int presentDays, int absentDays, int totalSchoolDays, double attendancePercentage) {
        this.presentDays = presentDays;
        this.absentDays = absentDays;
        this.totalSchoolDays = totalSchoolDays;
        this.attendancePercentage = attendancePercentage;
    }

    public int getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(int presentDays) {
        this.presentDays = presentDays;
    }

    public int getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(int absentDays) {
        this.absentDays = absentDays;
    }

    public int getTotalSchoolDays() {
        return totalSchoolDays;
    }

    public void setTotalSchoolDays(int totalSchoolDays) {
        this.totalSchoolDays = totalSchoolDays;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}
