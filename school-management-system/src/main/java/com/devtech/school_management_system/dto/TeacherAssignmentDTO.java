package com.devtech.school_management_system.dto;

public class TeacherAssignmentDTO {
    private Long teacherId;
    private Long subjectId;
    private String form;
    private String section;
    private String academicYear;

    public TeacherAssignmentDTO() {
    }

    public TeacherAssignmentDTO(Long teacherId, Long subjectId, String form, String section, String academicYear) {
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.form = form;
        this.section = section;
        this.academicYear = academicYear;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
}