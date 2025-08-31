package com.devtech.school_management_system.dto;

import com.devtech.school_management_system.entity.AiTemplate;
import com.devtech.school_management_system.entity.AiGeneratedContent;

import java.time.LocalDateTime;

public class AiTemplateDTO {
    private Long id;
    private Long teacherId;
    private String teacherName;
    private String name;
    private String description;
    private AiGeneratedContent.ContentType contentType;
    private AiGeneratedContent.DifficultyLevel difficultyLevel;
    private String formLevel;
    private Integer estimatedDuration;
    private Integer totalMarks;
    private String additionalInstructions;
    private boolean isPublic;
    private Integer usageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AiTemplateDTO() {}

    public AiTemplateDTO(AiTemplate template) {
        this.id = template.getId();
        this.teacherId = template.getTeacher().getId();
        this.teacherName = template.getTeacher().getFirstName() + " " + template.getTeacher().getLastName();
        this.name = template.getName();
        this.description = template.getDescription();
        this.contentType = template.getContentType();
        this.difficultyLevel = template.getDifficultyLevel();
        this.formLevel = template.getFormLevel();
        this.estimatedDuration = template.getEstimatedDuration();
        this.totalMarks = template.getTotalMarks();
        this.additionalInstructions = template.getAdditionalInstructions();
        this.isPublic = template.isPublic();
        this.usageCount = template.getUsageCount();
        this.createdAt = template.getCreatedAt();
        this.updatedAt = template.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AiGeneratedContent.ContentType getContentType() { return contentType; }
    public void setContentType(AiGeneratedContent.ContentType contentType) { this.contentType = contentType; }

    public AiGeneratedContent.DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(AiGeneratedContent.DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getFormLevel() { return formLevel; }
    public void setFormLevel(String formLevel) { this.formLevel = formLevel; }

    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }

    public String getAdditionalInstructions() { return additionalInstructions; }
    public void setAdditionalInstructions(String additionalInstructions) { this.additionalInstructions = additionalInstructions; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
