package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.AiContentGenerationRequest;
import com.devtech.school_management_system.dto.AiGeneratedContentDTO;
import com.devtech.school_management_system.dto.AiResourceDTO;
import com.devtech.school_management_system.entity.*;
import com.devtech.school_management_system.repository.AiGeneratedContentRepository;
import com.devtech.school_management_system.repository.AiResourceRepository;
import com.devtech.school_management_system.repository.AiUsageLogRepository;
import com.devtech.school_management_system.repository.SubjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

@Service
public class AiService {

    @Autowired
    private AiResourceRepository aiResourceRepository;

    @Autowired
    private AiGeneratedContentRepository aiGeneratedContentRepository;

    @Autowired
    private AiUsageLogRepository aiUsageLogRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpenAiService openAiService;

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    @Value("${app.ai.model.version:gpt-4}")
    private String aiModelVersion;

    @Value("${app.ai.api.key:}")
    private String aiApiKey;

    @Value("${app.ai.api.url:https://api.openai.com/v1/chat/completions}")
    private String aiApiUrl;

    // AI Resource Management
    @Transactional
    public AiResourceDTO uploadResource(MultipartFile file, AiResource.ResourceType type, 
                                      Long subjectId, String title, String description, 
                                      String academicYear, String formLevel, Teacher teacher) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Create upload directory if it doesn't exist
            Path uploadDir = Paths.get(uploadPath, "ai-resources");
            Files.createDirectories(uploadDir);
            
            // Save file
            Path filePath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            // Get subject
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

            // Create AI resource
            AiResource resource = new AiResource();
            resource.setTeacher(teacher);
            resource.setSubject(subject);
            resource.setTitle(title);
            resource.setDescription(description);
            resource.setType(type);
            resource.setFilePath(filePath.toString());
            resource.setFileName(originalFilename);
            resource.setFileSize(file.getSize());
            resource.setFileType(file.getContentType());
            resource.setAcademicYear(academicYear);
            resource.setFormLevel(formLevel);
            resource.setProcessed(false);
            resource.setProcessingStatus(AiResource.ProcessingStatus.PENDING);

            AiResource savedResource = aiResourceRepository.save(resource);

            // Log usage
            logAiUsage(teacher, "UPLOAD_RESOURCE", "RESOURCE", subjectId, 0L, 0L, "gpt-4", 0, true, null);

            return new AiResourceDTO(savedResource);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AiResourceDTO> getTeacherResources(Long teacherId) {
        List<AiResource> resources = aiResourceRepository.findByTeacherId(teacherId);
        return resources.stream()
                .map(AiResourceDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AiResourceDTO> getProcessedResourcesBySubject(Long subjectId) {
        List<AiResource> resources = aiResourceRepository.findProcessedResourcesBySubject(subjectId);
        return resources.stream()
                .map(AiResourceDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AiResourceDTO updateResource(Long resourceId, String title, String description, 
                                       String academicYear, String formLevel, Teacher teacher) {
        AiResource resource = aiResourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));

        // Verify ownership
        if (!resource.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You can only update your own resources");
        }

        resource.setTitle(title);
        resource.setDescription(description);
        resource.setAcademicYear(academicYear);
        resource.setFormLevel(formLevel);

        AiResource savedResource = aiResourceRepository.save(resource);
        return new AiResourceDTO(savedResource);
    }

    @Transactional
    public void deleteResource(Long resourceId, Teacher teacher) {
        AiResource resource = aiResourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));

        // Verify ownership
        if (!resource.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You can only delete your own resources");
        }

        // Delete the physical file if it exists
        try {
            Path filePath = Paths.get(resource.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Log the error but continue with database deletion
            System.err.println("Failed to delete physical file: " + e.getMessage());
        }

        aiResourceRepository.delete(resource);
    }

    @Transactional(readOnly = true)
    public byte[] downloadResource(Long resourceId, Teacher teacher) {
        AiResource resource = aiResourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));

        // Verify ownership
        if (!resource.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You can only download your own resources");
        }

        try {
            Path filePath = Paths.get(resource.getFilePath());
            if (!Files.exists(filePath)) {
                throw new IllegalArgumentException("Resource file not found on disk");
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource file", e);
        }
    }

    // AI Content Generation
    @Transactional
    public AiGeneratedContentDTO generateContent(AiContentGenerationRequest request, Teacher teacher) {
        try {
            // Validate request
            if (request.getSubjectId() == null || request.getTitle() == null || request.getContentType() == null) {
                throw new IllegalArgumentException("Missing required fields");
            }

            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

            // Get processed resources for context
            List<AiResource> processedResources = aiResourceRepository
                    .findProcessedResourcesByTeacherAndSubject(teacher.getId(), request.getSubjectId());

            // Generate content using AI (this would integrate with actual AI API)
            String generatedContent = generateContentWithAI(request, processedResources);
            String markingScheme = request.getIncludeMarkingScheme() != null && request.getIncludeMarkingScheme() 
                    ? generateMarkingSchemeWithAI(request, generatedContent) : null;

            // Create AI generated content
            AiGeneratedContent content = new AiGeneratedContent();
            content.setTeacher(teacher);
            content.setSubject(subject);
            content.setTitle(request.getTitle());
            content.setDescription(request.getDescription());
            content.setType(request.getContentType());
            content.setContentData(generatedContent);
            content.setMarkingScheme(markingScheme);
            content.setTopicFocus(request.getTopicFocus());
            content.setDifficultyLevel(request.getDifficultyLevel());
            content.setAcademicYear(request.getAcademicYear());
            content.setFormLevel(request.getFormLevel());
            content.setEstimatedDuration(request.getEstimatedDuration());
            content.setTotalMarks(request.getTotalMarks());
            content.setPublished(false);
            content.setUsageCount(0);
            content.setAiModelVersion(aiModelVersion);
            content.setGenerationParameters(objectMapper.writeValueAsString(request));

            AiGeneratedContent savedContent = aiGeneratedContentRepository.save(content);

            // Log usage
            logAiUsage(teacher, "GENERATE_CONTENT", request.getContentType().toString(), 
                    request.getSubjectId(), 1000L, 5000L, aiModelVersion, 50, true, null);

            return new AiGeneratedContentDTO(savedContent);

        } catch (Exception e) {
            // Log failed usage
            logAiUsage(teacher, "GENERATE_CONTENT", request.getContentType().toString(), 
                    request.getSubjectId(), 0L, 0L, aiModelVersion, 0, false, e.getMessage());
            throw new RuntimeException("Failed to generate content", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AiGeneratedContentDTO> getTeacherGeneratedContent(Long teacherId) {
        List<AiGeneratedContent> contents = aiGeneratedContentRepository.findByTeacherId(teacherId);
        return contents.stream()
                .map(AiGeneratedContentDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AiGeneratedContentDTO> getPublishedContentBySubject(Long subjectId) {
        List<AiGeneratedContent> contents = aiGeneratedContentRepository.findBySubjectIdAndPublished(subjectId, true);
        return contents.stream()
                .map(AiGeneratedContentDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AiGeneratedContentDTO> getPublishedContentForStudents() {
        List<AiGeneratedContent> contents = aiGeneratedContentRepository.findByPublished(true);
        return contents.stream()
                .map(AiGeneratedContentDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AiGeneratedContent getContentById(Long contentId) {
        return aiGeneratedContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));
    }

    @Transactional
    public AiGeneratedContentDTO publishContent(Long contentId, Teacher teacher) {
        AiGeneratedContent content = aiGeneratedContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        // Verify ownership
        if (!content.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You can only publish your own content");
        }

        content.setPublished(true);
        AiGeneratedContent savedContent = aiGeneratedContentRepository.save(content);
        return new AiGeneratedContentDTO(savedContent);
    }

    @Transactional
    public void incrementUsageCount(Long contentId) {
        aiGeneratedContentRepository.findById(contentId).ifPresent(content -> {
            content.setUsageCount(content.getUsageCount() + 1);
            aiGeneratedContentRepository.save(content);
        });
    }

    @Transactional
    public AiGeneratedContentDTO updateContent(Long contentId, String title, String description, 
                                             boolean published, Teacher teacher) {
        AiGeneratedContent content = aiGeneratedContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        // Verify ownership
        if (!content.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You can only update your own content");
        }

        content.setTitle(title);
        content.setDescription(description);
        content.setPublished(published);

        AiGeneratedContent savedContent = aiGeneratedContentRepository.save(content);
        return new AiGeneratedContentDTO(savedContent);
    }

    @Transactional
    public void deleteContent(Long contentId, Teacher teacher) {
        AiGeneratedContent content = aiGeneratedContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        // Verify ownership
        if (!content.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You can only delete your own content");
        }

        aiGeneratedContentRepository.delete(content);
    }

    @Transactional(readOnly = true)
    public byte[] exportContent(Long contentId, String format, Teacher teacher) {
        AiGeneratedContent content = aiGeneratedContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        // Verify ownership
        if (!content.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You can only export your own content");
        }

        // Generate export based on format
        String exportText = generateExportText(content, format);
        return exportText.getBytes();
    }

    private String generateExportText(AiGeneratedContent content, String format) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Title: ").append(content.getTitle()).append("\n");
        sb.append("Subject: ").append(content.getSubject().getName()).append("\n");
        sb.append("Type: ").append(content.getType()).append("\n");
        sb.append("Difficulty: ").append(content.getDifficultyLevel()).append("\n");
        sb.append("Academic Year: ").append(content.getAcademicYear()).append("\n");
        sb.append("Form Level: ").append(content.getFormLevel()).append("\n");
        
        if (content.getEstimatedDuration() != null) {
            sb.append("Duration: ").append(content.getEstimatedDuration()).append(" minutes\n");
        }
        
        if (content.getTotalMarks() != null) {
            sb.append("Total Marks: ").append(content.getTotalMarks()).append("\n");
        }
        
        sb.append("\n");
        
        if (content.getDescription() != null) {
            sb.append("Description:\n").append(content.getDescription()).append("\n\n");
        }
        
        sb.append("Content:\n").append(content.getContentData()).append("\n");
        
        if (content.getMarkingScheme() != null) {
            sb.append("\nMarking Scheme:\n").append(content.getMarkingScheme()).append("\n");
        }
        
        return sb.toString();
    }

    // AI Usage Analytics
    @Transactional(readOnly = true)
    public List<Object[]> getTeacherUsageStats(Long teacherId) {
        return aiUsageLogRepository.getOperationCountsByTeacher(teacherId);
    }

    @Transactional(readOnly = true)
    public Long getTeacherTotalTokens(Long teacherId, LocalDateTime since) {
        return aiUsageLogRepository.getTotalTokensUsedByTeacherSince(teacherId, since);
    }

    @Transactional(readOnly = true)
    public Long getTeacherTotalCost(Long teacherId, LocalDateTime since) {
        return aiUsageLogRepository.getTotalCostByTeacherSince(teacherId, since);
    }

    // Private helper methods
    private String generateContentWithAI(AiContentGenerationRequest request, List<AiResource> resources) {
        try {
            // Build context from uploaded resources
            StringBuilder context = new StringBuilder();
            context.append("Generate ").append(request.getContentType()).append(" for ");
            context.append("Subject ID: ").append(request.getSubjectId());
            
            if (request.getTopicFocus() != null) {
                context.append("\nTopic Focus: ").append(request.getTopicFocus());
            }
            
            context.append("\nDifficulty Level: ").append(request.getDifficultyLevel());
            context.append("\nAcademic Year: ").append(request.getAcademicYear());
            context.append("\nForm Level: ").append(request.getFormLevel());
            
            if (request.getEstimatedDuration() != null) {
                context.append("\nEstimated Duration: ").append(request.getEstimatedDuration()).append(" minutes");
            }
            
            if (request.getTotalMarks() != null) {
                context.append("\nTotal Marks: ").append(request.getTotalMarks());
            }
            
            if (request.getAdditionalInstructions() != null) {
                context.append("\nAdditional Instructions: ").append(request.getAdditionalInstructions());
            }
            
            // Add context from uploaded resources
            if (!resources.isEmpty()) {
                context.append("\n\nContext from uploaded resources:");
                for (AiResource resource : resources) {
                    context.append("\n- ").append(resource.getType()).append(": ").append(resource.getTitle());
                    if (resource.getDescription() != null) {
                        context.append(" - ").append(resource.getDescription());
                    }
                }
            }

            // Try to use real AI API if configured
            if (aiApiKey != null && !aiApiKey.trim().isEmpty()) {
                return callOpenAIAPI(context.toString(), request.getContentType());
            } else {
                // Fallback to mock response
                return generateMockResponse(context.toString(), request.getContentType());
            }
        } catch (Exception e) {
            // Log error and fallback to mock response
            System.err.println("Error calling AI API: " + e.getMessage());
            return generateMockResponse("Error occurred, using mock response", request.getContentType());
        }
    }

    private String callOpenAIAPI(String prompt, AiGeneratedContent.ContentType contentType) {
        try {
            String systemPrompt = buildSystemPrompt(contentType);
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemPrompt));
            messages.add(new ChatMessage("user", prompt));
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-4o-mini")
                    .messages(messages)
                    .maxTokens(4000)
                    .temperature(0.7)
                    .build();
            
            String response = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call OpenAI API", e);
        }
    }

    private String buildSystemPrompt(AiGeneratedContent.ContentType contentType) {
        switch (contentType) {
            case STUDY_NOTES:
                return "You are an expert educational content creator. Create comprehensive study notes that are clear, well-structured, and suitable for the specified academic level.";
            case PRACTICE_QUESTIONS:
                return "You are an expert educator. Create practice questions that test understanding and application of concepts at the specified difficulty level.";
            case QUIZ:
                return "You are an expert quiz creator. Create engaging quiz questions that assess knowledge and understanding.";
            case MIDTERM_EXAM:
                return "You are an expert exam creator. Create a comprehensive midterm exam that thoroughly tests the covered material.";
            case FINAL_EXAM:
                return "You are an expert exam creator. Create a comprehensive final exam that tests all major concepts and learning objectives.";
            case ASSIGNMENT:
                return "You are an expert assignment creator. Create meaningful assignments that promote learning and critical thinking.";
            case REVISION_MATERIAL:
                return "You are an expert revision material creator. Create comprehensive revision materials that help students prepare for assessments.";
            default:
                return "You are an expert educational content creator. Create high-quality educational content suitable for the specified academic level.";
        }
    }

    private String generateMockResponse(String context, AiGeneratedContent.ContentType contentType) {
        return "Mock Generated " + contentType + ":\n\n" +
               "Context: " + context + "\n\n" +
               "This is a sample generated content. In a real implementation, this would be " +
               "generated by an AI model based on the uploaded resources and specifications.\n\n" +
               "Content Type: " + contentType + "\n" +
               "Generated at: " + LocalDateTime.now() + "\n" +
               "Model: Mock AI Service";
    }

    private String generateMarkingSchemeWithAI(AiContentGenerationRequest request, String content) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Generate a comprehensive marking scheme for the following content:\n\n");
            prompt.append("Content Type: ").append(request.getContentType()).append("\n");
            prompt.append("Difficulty Level: ").append(request.getDifficultyLevel()).append("\n");
            prompt.append("Total Marks: ").append(request.getTotalMarks()).append("\n");
            prompt.append("Form Level: ").append(request.getFormLevel()).append("\n\n");
            prompt.append("Content:\n").append(content).append("\n\n");
            prompt.append("Please provide a detailed marking scheme with specific criteria and mark allocations.");

            // Try to use real AI API if configured
            if (aiApiKey != null && !aiApiKey.trim().isEmpty()) {
                return callOpenAIMarkingSchemeAPI(prompt.toString());
            } else {
                return generateMockMarkingScheme(request, content);
            }
        } catch (Exception e) {
            System.err.println("Error generating marking scheme: " + e.getMessage());
            return generateMockMarkingScheme(request, content);
        }
    }

    private String callOpenAIMarkingSchemeAPI(String prompt) {
        try {
            String systemPrompt = "You are an expert educational assessment specialist. Create comprehensive marking schemes that are fair, detailed, and aligned with educational standards. Provide specific criteria and mark allocations for each section.";
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemPrompt));
            messages.add(new ChatMessage("user", prompt));
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-4o-mini")
                    .messages(messages)
                    .maxTokens(2000)
                    .temperature(0.5)
                    .build();
            
            String response = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call OpenAI API for marking scheme", e);
        }
    }

    private String generateMockMarkingScheme(AiContentGenerationRequest request, String content) {
        return "Mock Marking Scheme:\n\n" +
               "1. Content accuracy (40%)\n" +
               "2. Understanding and application (30%)\n" +
               "3. Presentation and clarity (20%)\n" +
               "4. Originality and creativity (10%)\n\n" +
               "Generated for: " + request.getContentType() + "\n" +
               "Difficulty: " + request.getDifficultyLevel() + "\n" +
               "Total Marks: " + request.getTotalMarks();
    }

    private void logAiUsage(Teacher teacher, String operation, String contentType, Long subjectId,
                           Long tokensUsed, Long processingTimeMs, String aiModelUsed, 
                           Integer costInCents, boolean success, String errorMessage) {
        AiUsageLog log = new AiUsageLog();
        log.setTeacher(teacher);
        log.setOperation(operation);
        log.setContentType(contentType);
        log.setSubjectId(subjectId);
        log.setTokensUsed(tokensUsed.intValue());
        log.setProcessingTimeMs(processingTimeMs);
        log.setAiModelUsed(aiModelUsed);
        log.setCostInCents(costInCents);
        log.setSuccess(success);
        log.setErrorMessage(errorMessage);
        log.setRequestParameters("{}"); // Could store actual request parameters as JSON

        aiUsageLogRepository.save(log);
    }

    // AI Model Configuration
    @Transactional(readOnly = true)
    public List<com.devtech.school_management_system.dto.AiModelConfigDTO> getAvailableModels() {
        List<com.devtech.school_management_system.dto.AiModelConfigDTO> models = new ArrayList<>();
        
        // OpenAI Models
        models.add(new com.devtech.school_management_system.dto.AiModelConfigDTO(
            "gpt-4", "GPT-4", "OpenAI", 
            "Most capable model for complex educational content generation", 
            true, 0.03, 8192, "Content generation, analysis, problem-solving"
        ));
        
        models.add(new com.devtech.school_management_system.dto.AiModelConfigDTO(
            "gpt-4-turbo", "GPT-4 Turbo", "OpenAI", 
            "Faster and more cost-effective version of GPT-4", 
            true, 0.01, 128000, "Content generation, analysis, problem-solving"
        ));
        
        models.add(new com.devtech.school_management_system.dto.AiModelConfigDTO(
            "gpt-3.5-turbo", "GPT-3.5 Turbo", "OpenAI", 
            "Fast and efficient model for basic educational content", 
            true, 0.002, 16385, "Content generation, basic analysis"
        ));
        
        // Claude Models (if available)
        models.add(new com.devtech.school_management_system.dto.AiModelConfigDTO(
            "claude-3-opus", "Claude 3 Opus", "Anthropic", 
            "Most capable Claude model for complex educational tasks", 
            false, 0.015, 200000, "Content generation, analysis, reasoning"
        ));
        
        models.add(new com.devtech.school_management_system.dto.AiModelConfigDTO(
            "claude-3-sonnet", "Claude 3 Sonnet", "Anthropic", 
            "Balanced Claude model for educational content", 
            false, 0.003, 200000, "Content generation, analysis"
        ));
        
        // Mock Model for testing
        models.add(new com.devtech.school_management_system.dto.AiModelConfigDTO(
            "mock-ai", "Mock AI Service", "Internal", 
            "Mock AI service for testing and development", 
            true, 0.0, 10000, "Content generation, testing"
        ));
        
        return models;
    }

    @Transactional
    public void selectModelForTeacher(Long teacherId, String modelId) {
        // In a real implementation, you would store this preference in the database
        // For now, we'll just validate that the model exists
        List<com.devtech.school_management_system.dto.AiModelConfigDTO> availableModels = getAvailableModels();
        boolean modelExists = availableModels.stream()
            .anyMatch(model -> model.getModelId().equals(modelId) && model.isAvailable());
        
        if (!modelExists) {
            throw new IllegalArgumentException("Model not available: " + modelId);
        }
        
        // Log the model selection
        Teacher teacher = teacherService.getTeacherById(teacherId);
        logAiUsage(teacher, "SELECT_MODEL", "MODEL_SELECTION", null, 
                   0L, 0L, modelId, 0, true, null);
    }

    @Transactional(readOnly = true)
    public com.devtech.school_management_system.dto.AiModelConfigDTO getCurrentModelForTeacher(Long teacherId) {
        // In a real implementation, you would retrieve this from the database
        // For now, return the default model
        return new com.devtech.school_management_system.dto.AiModelConfigDTO(
            "gpt-4", "GPT-4", "OpenAI", 
            "Most capable model for complex educational content generation", 
            true, 0.03, 8192, "Content generation, analysis, problem-solving"
        );
    }
}
