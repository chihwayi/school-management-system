package com.devtech.school_management_system.dto;

import com.devtech.school_management_system.entity.AiGeneratedContent;

public class AiContentGenerationRequest {
    private Long subjectId;
    private String title;
    private String description;
    private AiGeneratedContent.ContentType contentType;
    private String topicFocus;
    private AiGeneratedContent.DifficultyLevel difficultyLevel;
    private String academicYear;
    private String formLevel;
    private Integer estimatedDuration;
    private Integer totalMarks;
    private String additionalInstructions;
    private String syllabusScope;
    private String targetAudience;
    private Boolean includeMarkingScheme;
    private Boolean includeExplanations;

    // Constructors
    public AiContentGenerationRequest() {}

    // Getters and Setters
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AiGeneratedContent.ContentType getContentType() { return contentType; }
    public void setContentType(AiGeneratedContent.ContentType contentType) { this.contentType = contentType; }

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

    public String getAdditionalInstructions() { return additionalInstructions; }
    public void setAdditionalInstructions(String additionalInstructions) { this.additionalInstructions = additionalInstructions; }

    public String getSyllabusScope() { return syllabusScope; }
    public void setSyllabusScope(String syllabusScope) { this.syllabusScope = syllabusScope; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public Boolean getIncludeMarkingScheme() { return includeMarkingScheme; }
    public void setIncludeMarkingScheme(Boolean includeMarkingScheme) { this.includeMarkingScheme = includeMarkingScheme; }

    public Boolean getIncludeExplanations() { return includeExplanations; }
    public void setIncludeExplanations(Boolean includeExplanations) { this.includeExplanations = includeExplanations; }
}
