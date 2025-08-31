package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.AiContentGenerationRequest;
import com.devtech.school_management_system.dto.AiGeneratedContentDTO;
import com.devtech.school_management_system.dto.AiResourceDTO;
import com.devtech.school_management_system.entity.AiResource;
import com.devtech.school_management_system.entity.Teacher;
import com.devtech.school_management_system.entity.AiGeneratedContent;
import com.devtech.school_management_system.entity.Student;
import com.devtech.school_management_system.service.AiService;
import com.devtech.school_management_system.service.TeacherService;
import com.devtech.school_management_system.service.AiWhatsAppSharingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/ai", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasRole('TEACHER')")
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AiWhatsAppSharingService aiWhatsAppSharingService;

    // AI Resource Management
    @PostMapping("/resources/upload")
    public ResponseEntity<AiResourceDTO> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") AiResource.ResourceType type,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("academicYear") String academicYear,
            @RequestParam(value = "formLevel", required = false) String formLevel) {
        
        Teacher teacher = getCurrentTeacher();
        AiResourceDTO resource = aiService.uploadResource(file, type, subjectId, title, 
                                                        description, academicYear, formLevel, teacher);
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/resources/teacher")
    public ResponseEntity<List<AiResourceDTO>> getTeacherResources() {
        Teacher teacher = getCurrentTeacher();
        List<AiResourceDTO> resources = aiService.getTeacherResources(teacher.getId());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/resources/subject/{subjectId}")
    public ResponseEntity<List<AiResourceDTO>> getProcessedResourcesBySubject(@PathVariable Long subjectId) {
        List<AiResourceDTO> resources = aiService.getProcessedResourcesBySubject(subjectId);
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/resources/{resourceId}")
    public ResponseEntity<AiResourceDTO> updateResource(
            @PathVariable Long resourceId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("academicYear") String academicYear,
            @RequestParam("formLevel") String formLevel) {
        
        Teacher teacher = getCurrentTeacher();
        AiResourceDTO resource = aiService.updateResource(resourceId, title, description, academicYear, formLevel, teacher);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/resources/{resourceId}")
    public ResponseEntity<Map<String, String>> deleteResource(@PathVariable Long resourceId) {
        Teacher teacher = getCurrentTeacher();
        aiService.deleteResource(resourceId, teacher);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Resource deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resources/{resourceId}/download")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long resourceId) {
        Teacher teacher = getCurrentTeacher();
        byte[] fileData = aiService.downloadResource(resourceId, teacher);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=resource")
                .body(fileData);
    }

    // AI Content Generation
    @PostMapping("/content/generate")
    public ResponseEntity<AiGeneratedContentDTO> generateContent(@RequestBody AiContentGenerationRequest request) {
        Teacher teacher = getCurrentTeacher();
        AiGeneratedContentDTO content = aiService.generateContent(request, teacher);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/content/teacher")
    public ResponseEntity<List<AiGeneratedContentDTO>> getTeacherGeneratedContent() {
        Teacher teacher = getCurrentTeacher();
        List<AiGeneratedContentDTO> contents = aiService.getTeacherGeneratedContent(teacher.getId());
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/content/published/subject/{subjectId}")
    public ResponseEntity<List<AiGeneratedContentDTO>> getPublishedContentBySubject(@PathVariable Long subjectId) {
        List<AiGeneratedContentDTO> contents = aiService.getPublishedContentBySubject(subjectId);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/content/published/students")
    public ResponseEntity<List<AiGeneratedContentDTO>> getPublishedContentForStudents() {
        List<AiGeneratedContentDTO> content = aiService.getPublishedContentForStudents();
        return ResponseEntity.ok(content);
    }

    @PostMapping("/content/{contentId}/publish")
    public ResponseEntity<AiGeneratedContentDTO> publishContent(@PathVariable Long contentId) {
        Teacher teacher = getCurrentTeacher();
        AiGeneratedContentDTO content = aiService.publishContent(contentId, teacher);
        return ResponseEntity.ok(content);
    }

    @PostMapping("/content/{contentId}/use")
    public ResponseEntity<Map<String, String>> useContent(@PathVariable Long contentId) {
        aiService.incrementUsageCount(contentId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Content usage recorded successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/content/{contentId}")
    public ResponseEntity<AiGeneratedContentDTO> updateContent(
            @PathVariable Long contentId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("published") boolean published) {
        
        Teacher teacher = getCurrentTeacher();
        AiGeneratedContentDTO content = aiService.updateContent(contentId, title, description, published, teacher);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/content/{contentId}")
    public ResponseEntity<Map<String, String>> deleteContent(@PathVariable Long contentId) {
        Teacher teacher = getCurrentTeacher();
        aiService.deleteContent(contentId, teacher);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Content deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/content/{contentId}/export")
    public ResponseEntity<byte[]> exportContent(
            @PathVariable Long contentId,
            @RequestParam("format") String format) {
        
        Teacher teacher = getCurrentTeacher();
        byte[] exportData = aiService.exportContent(contentId, format, teacher);
        
        String filename = "content-" + contentId + "." + format;
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(exportData);
    }

    // AI Usage Analytics
    @GetMapping("/analytics/usage")
    public ResponseEntity<Map<String, Object>> getTeacherUsageStats() {
        Teacher teacher = getCurrentTeacher();
        List<Object[]> usageStats = aiService.getTeacherUsageStats(teacher.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("teacherId", teacher.getId());
        response.put("usageStats", usageStats);
        
        // Get monthly stats
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Long totalTokens = aiService.getTeacherTotalTokens(teacher.getId(), oneMonthAgo);
        Long totalCost = aiService.getTeacherTotalCost(teacher.getId(), oneMonthAgo);
        
        response.put("monthlyTokens", totalTokens != null ? totalTokens : 0);
        response.put("monthlyCost", totalCost != null ? totalCost : 0);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/usage/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyUsageStats() {
        Teacher teacher = getCurrentTeacher();
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        
        Map<String, Object> response = new HashMap<>();
        response.put("teacherId", teacher.getId());
        response.put("period", "Last 30 days");
        response.put("totalTokens", aiService.getTeacherTotalTokens(teacher.getId(), oneMonthAgo));
        response.put("totalCost", aiService.getTeacherTotalCost(teacher.getId(), oneMonthAgo));
        
        return ResponseEntity.ok(response);
    }

    // AI Resource Types
    @GetMapping("/resource-types")
    public ResponseEntity<AiResource.ResourceType[]> getResourceTypes() {
        return ResponseEntity.ok(AiResource.ResourceType.values());
    }

    // WhatsApp Sharing Endpoints
    @PostMapping("/content/{contentId}/share-whatsapp/class")
    public ResponseEntity<Map<String, String>> shareContentToClass(
            @PathVariable Long contentId,
            @RequestParam String form,
            @RequestParam String section) {
        
        Teacher teacher = getCurrentTeacher();
        AiGeneratedContent content = aiService.getContentById(contentId);
        
        if (!content.getTeacher().getId().equals(teacher.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only share your own content"));
        }
        
        aiWhatsAppSharingService.shareContentToClass(content, teacher, form, section);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Content shared successfully to class " + form + " " + section);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/content/{contentId}/share-whatsapp/classes")
    public ResponseEntity<Map<String, String>> shareContentToMultipleClasses(
            @PathVariable Long contentId,
            @RequestBody List<String> classList) {
        
        Teacher teacher = getCurrentTeacher();
        AiGeneratedContent content = aiService.getContentById(contentId);
        
        if (!content.getTeacher().getId().equals(teacher.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only share your own content"));
        }
        
        aiWhatsAppSharingService.shareContentToMultipleClasses(content, teacher, classList);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Content shared successfully to " + classList.size() + " classes");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/content/{contentId}/share-whatsapp/students")
    public ResponseEntity<Map<String, String>> shareContentToSpecificStudents(
            @PathVariable Long contentId,
            @RequestBody List<Long> studentIds) {
        
        Teacher teacher = getCurrentTeacher();
        AiGeneratedContent content = aiService.getContentById(contentId);
        
        if (!content.getTeacher().getId().equals(teacher.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only share your own content"));
        }
        
        aiWhatsAppSharingService.shareContentToSpecificStudents(content, teacher, studentIds);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Content shared successfully to " + studentIds.size() + " students");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/whatsapp/stats/{form}/{section}")
    public ResponseEntity<AiWhatsAppSharingService.WhatsAppStats> getWhatsAppStats(
            @PathVariable String form,
            @PathVariable String section,
            @RequestParam String academicYear) {
        
        AiWhatsAppSharingService.WhatsAppStats stats = aiWhatsAppSharingService.getWhatsAppStats(form, section, academicYear);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/whatsapp/students/{form}/{section}")
    public ResponseEntity<List<Student>> getStudentsWithWhatsApp(
            @PathVariable String form,
            @PathVariable String section,
            @RequestParam String academicYear) {
        
        List<Student> students = aiWhatsAppSharingService.getStudentsWithWhatsApp(form, section, academicYear);
        return ResponseEntity.ok(students);
    }

    // AI Content Types
    @GetMapping("/content-types")
    public ResponseEntity<com.devtech.school_management_system.entity.AiGeneratedContent.ContentType[]> getContentTypes() {
        return ResponseEntity.ok(com.devtech.school_management_system.entity.AiGeneratedContent.ContentType.values());
    }

    // AI Difficulty Levels
    @GetMapping("/difficulty-levels")
    public ResponseEntity<com.devtech.school_management_system.entity.AiGeneratedContent.DifficultyLevel[]> getDifficultyLevels() {
        return ResponseEntity.ok(com.devtech.school_management_system.entity.AiGeneratedContent.DifficultyLevel.values());
    }

    // AI Model Configuration
    @GetMapping("/models")
    public ResponseEntity<List<com.devtech.school_management_system.dto.AiModelConfigDTO>> getAvailableModels() {
        List<com.devtech.school_management_system.dto.AiModelConfigDTO> models = aiService.getAvailableModels();
        return ResponseEntity.ok(models);
    }

    @PostMapping("/models/{modelId}/select")
    public ResponseEntity<Map<String, String>> selectModel(@PathVariable String modelId) {
        Teacher teacher = getCurrentTeacher();
        aiService.selectModelForTeacher(teacher.getId(), modelId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Model selected successfully");
        response.put("modelId", modelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/models/current")
    public ResponseEntity<com.devtech.school_management_system.dto.AiModelConfigDTO> getCurrentModel() {
        Teacher teacher = getCurrentTeacher();
        com.devtech.school_management_system.dto.AiModelConfigDTO model = aiService.getCurrentModelForTeacher(teacher.getId());
        return ResponseEntity.ok(model);
    }

    // Health check for AI service
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "AI Service");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // Helper method to get current teacher
    private Teacher getCurrentTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return teacherService.getTeacherByUsername(username);
    }
}
