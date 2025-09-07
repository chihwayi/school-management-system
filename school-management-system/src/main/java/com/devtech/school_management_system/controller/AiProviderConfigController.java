package com.devtech.school_management_system.controller;

import com.devtech.school_management_system.dto.AiProviderConfigDTO;
import com.devtech.school_management_system.service.AiProviderConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/ai-providers")
@PreAuthorize("hasRole('ADMIN')")
public class AiProviderConfigController {

    @Autowired
    private AiProviderConfigService aiProviderConfigService;

    @GetMapping
    public ResponseEntity<List<AiProviderConfigDTO>> getAllProviderConfigs() {
        List<AiProviderConfigDTO> configs = aiProviderConfigService.getAllProviderConfigs();
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/configured")
    public ResponseEntity<List<AiProviderConfigDTO>> getConfiguredProviders() {
        List<AiProviderConfigDTO> configs = aiProviderConfigService.getConfiguredProviders();
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<AiProviderConfigDTO>> getEnabledProviders() {
        List<AiProviderConfigDTO> configs = aiProviderConfigService.getEnabledProviders();
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AiProviderConfigDTO> getProviderConfig(@PathVariable Long id) {
        return aiProviderConfigService.getProviderConfig(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{providerName}")
    public ResponseEntity<AiProviderConfigDTO> getProviderConfigByName(@PathVariable String providerName) {
        return aiProviderConfigService.getProviderConfigByName(providerName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AiProviderConfigDTO> createProviderConfig(@RequestBody AiProviderConfigDTO configDTO) {
        AiProviderConfigDTO createdConfig = aiProviderConfigService.createProviderConfig(configDTO);
        return ResponseEntity.ok(createdConfig);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AiProviderConfigDTO> updateProviderConfig(@PathVariable Long id, @RequestBody AiProviderConfigDTO configDTO) {
        try {
            AiProviderConfigDTO updatedConfig = aiProviderConfigService.updateProviderConfig(id, configDTO);
            return ResponseEntity.ok(updatedConfig);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProviderConfig(@PathVariable Long id) {
        try {
            aiProviderConfigService.deleteProviderConfig(id);
            return ResponseEntity.ok(Map.of("message", "Provider configuration deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Map<String, String>> toggleProviderStatus(@PathVariable Long id) {
        try {
            aiProviderConfigService.toggleProviderStatus(id);
            return ResponseEntity.ok(Map.of("message", "Provider status toggled successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/initialize")
    public ResponseEntity<Map<String, String>> initializeDefaultProviders() {
        aiProviderConfigService.initializeDefaultProviders();
        return ResponseEntity.ok(Map.of("message", "Default providers initialized successfully"));
    }

    @GetMapping("/{providerName}/configured")
    public ResponseEntity<Map<String, Boolean>> isProviderConfigured(@PathVariable String providerName) {
        boolean isConfigured = aiProviderConfigService.isProviderConfigured(providerName);
        return ResponseEntity.ok(Map.of("configured", isConfigured));
    }
}
