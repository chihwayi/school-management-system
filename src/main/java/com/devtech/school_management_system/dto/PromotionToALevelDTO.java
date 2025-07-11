package com.devtech.school_management_system.dto;

import java.util.List;

public class PromotionToALevelDTO {
    private List<Long> studentIds;
    private List<Long> subjectIds;
    private String form;
    private String section;

    public PromotionToALevelDTO() {
    }

    public PromotionToALevelDTO(List<Long> studentIds, List<Long> subjectIds, String form, String section) {
        this.studentIds = studentIds;
        this.subjectIds = subjectIds;
        this.form = form;
        this.section = section;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }

    public List<Long> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(List<Long> subjectIds) {
        this.subjectIds = subjectIds;
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
}
