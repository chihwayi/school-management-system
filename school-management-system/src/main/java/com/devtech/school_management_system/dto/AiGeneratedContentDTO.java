package com.devtech.school_management_system.dto;

import com.devtech.school_management_system.entity.AiGeneratedContent;

import java.time.LocalDateTime;

public class AiGeneratedContentDTO {
    private Long id;
    private Long teacherId;
    private String teacherName;
    private Long subjectId;
    private String subjectName;
    private String title;
    private String description;
    private AiGeneratedContent.ContentType type;
    private String contentData;
    private String markingScheme;
    private String topicFocus;
    private AiGeneratedContent.DifficultyLevel difficultyLevel;
    private String academicYear;
    private String formLevel;
    private Integer estimatedDuration;
    private Integer totalMarks;
    private boolean published;
    private Integer usageCount;
    private String aiModelVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AiGeneratedContentDTO() {}

    public AiGeneratedContentDTO(AiGeneratedContent content) {
        this.id = content.getId();
        this.teacherId = content.getTeacher().getId();
        this.teacherName = content.getTeacher().getFirstName() + " " + content.getTeacher().getLastName();
        this.subjectId = content.getSubject().getId();
        this.subjectName = content.getSubject().getName();
        this.title = content.getTitle();
        this.description = content.getDescription();
        this.type = content.getType();
        this.contentData = content.getContentData();
        this.markingScheme = content.getMarkingScheme();
        this.topicFocus = content.getTopicFocus();
        this.difficultyLevel = content.getDifficultyLevel();
        this.academicYear = content.getAcademicYear();
        this.formLevel = content.getFormLevel();
        this.estimatedDuration = content.getEstimatedDuration();
        this.totalMarks = content.getTotalMarks();
        this.published = content.isPublished();
        this.usageCount = content.getUsageCount();
        this.aiModelVersion = content.getAiModelVersion();
        this.createdAt = content.getCreatedAt();
        this.updatedAt = content.getUpdatedAt();
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

    public AiGeneratedContent.ContentType getType() { return type; }
    public void setType(AiGeneratedContent.ContentType type) { this.type = type; }

    public String getContentData() { return contentData; }
    public void setContentData(String contentData) { this.contentData = contentData; }

    public String getMarkingScheme() { return markingScheme; }
    public void setMarkingScheme(String markingScheme) { this.markingScheme = markingScheme; }

    public String getTopicFocus() { return topicFocus; }
    public void setTopicFocus(String topicFocus) { this.topicFocus = topicFocus; }

    public AiGeneratedContent.DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(AiGeneratedContent.DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getFormLevel() { return formLevel; }
    public void setFormLevel(String formLevel) { this.formLevel = formLevel; }

    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public String getAiModelVersion() { return aiModelVersion; }
    public void setAiModelVersion(String aiModelVersion) { this.aiModelVersion = aiModelVersion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
