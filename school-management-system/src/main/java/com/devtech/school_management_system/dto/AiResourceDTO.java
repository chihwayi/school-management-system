package com.devtech.school_management_system.dto;

import com.devtech.school_management_system.entity.AiResource;

import java.time.LocalDateTime;

public class AiResourceDTO {
    private Long id;
    private Long teacherId;
    private String teacherName;
    private Long subjectId;
    private String subjectName;
    private String title;
    private String description;
    private AiResource.ResourceType type;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String academicYear;
    private String formLevel;
    private boolean processed;
    private AiResource.ProcessingStatus processingStatus;
    private String processingNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AiResourceDTO() {}

    public AiResourceDTO(AiResource resource) {
        this.id = resource.getId();
        this.teacherId = resource.getTeacher().getId();
        this.teacherName = resource.getTeacher().getFirstName() + " " + resource.getTeacher().getLastName();
        this.subjectId = resource.getSubject().getId();
        this.subjectName = resource.getSubject().getName();
        this.title = resource.getTitle();
        this.description = resource.getDescription();
        this.type = resource.getType();
        this.fileName = resource.getFileName();
        this.fileSize = resource.getFileSize();
        this.fileType = resource.getFileType();
        this.academicYear = resource.getAcademicYear();
        this.formLevel = resource.getFormLevel();
        this.processed = resource.isProcessed();
        this.processingStatus = resource.getProcessingStatus();
        this.processingNotes = resource.getProcessingNotes();
        this.createdAt = resource.getCreatedAt();
        this.updatedAt = resource.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AiResource.ResourceType getType() { return type; }
    public void setType(AiResource.ResourceType type) { this.type = type; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getFormLevel() { return formLevel; }
    public void setFormLevel(String formLevel) { this.formLevel = formLevel; }

    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }

    public AiResource.ProcessingStatus getProcessingStatus() { return processingStatus; }
    public void setProcessingStatus(AiResource.ProcessingStatus processingStatus) { this.processingStatus = processingStatus; }

    public String getProcessingNotes() { return processingNotes; }
    public void setProcessingNotes(String processingNotes) { this.processingNotes = processingNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
