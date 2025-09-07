package com.devtech.school_management_system.service;

import com.devtech.school_management_system.dto.AiProviderConfigDTO;
import com.devtech.school_management_system.entity.AiProviderConfig;
import com.devtech.school_management_system.repository.AiProviderConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AiProviderConfigService {

    @Autowired
    private AiProviderConfigRepository aiProviderConfigRepository;

    public List<AiProviderConfigDTO> getAllProviderConfigs() {
        return aiProviderConfigRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AiProviderConfigDTO> getConfiguredProviders() {
        return aiProviderConfigRepository.findConfiguredProviders().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AiProviderConfigDTO> getEnabledProviders() {
        return aiProviderConfigRepository.findByIsEnabledTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<AiProviderConfigDTO> getProviderConfig(Long id) {
        return aiProviderConfigRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<AiProviderConfigDTO> getProviderConfigByName(String providerName) {
        return aiProviderConfigRepository.findByProviderName(providerName)
                .map(this::convertToDTO);
    }

    public Optional<AiProviderConfig> getConfiguredProvider(String providerName) {
        return aiProviderConfigRepository.findConfiguredProvider(providerName);
    }

    public AiProviderConfigDTO createProviderConfig(AiProviderConfigDTO configDTO) {
        AiProviderConfig config = convertToEntity(configDTO);
        AiProviderConfig savedConfig = aiProviderConfigRepository.save(config);
        return convertToDTO(savedConfig);
    }

    public AiProviderConfigDTO updateProviderConfig(Long id, AiProviderConfigDTO configDTO) {
        AiProviderConfig existingConfig = aiProviderConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider config not found with id: " + id));

        // Update fields
        existingConfig.setDisplayName(configDTO.getDisplayName());
        existingConfig.setApiKey(configDTO.getApiKey());
        existingConfig.setApiUrl(configDTO.getApiUrl());
        existingConfig.setDefaultModel(configDTO.getDefaultModel());
        existingConfig.setIsEnabled(configDTO.getIsEnabled());
        existingConfig.setMaxTokens(configDTO.getMaxTokens());
        existingConfig.setTemperature(configDTO.getTemperature());
        existingConfig.setCostPer1kTokens(configDTO.getCostPer1kTokens());
        existingConfig.setDescription(configDTO.getDescription());
        existingConfig.setUseCases(configDTO.getUseCases());
        existingConfig.setConfigurationJson(configDTO.getConfigurationJson());

        AiProviderConfig savedConfig = aiProviderConfigRepository.save(existingConfig);
        return convertToDTO(savedConfig);
    }

    public void deleteProviderConfig(Long id) {
        if (!aiProviderConfigRepository.existsById(id)) {
            throw new RuntimeException("Provider config not found with id: " + id);
        }
        aiProviderConfigRepository.deleteById(id);
    }

    public void toggleProviderStatus(Long id) {
        AiProviderConfig config = aiProviderConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider config not found with id: " + id));
        config.setIsEnabled(!config.getIsEnabled());
        aiProviderConfigRepository.save(config);
    }

    public boolean isProviderConfigured(String providerName) {
        return aiProviderConfigRepository.findConfiguredProvider(providerName).isPresent();
    }

    public void initializeDefaultProviders() {
        // Initialize default provider configurations if they don't exist
        initializeProviderIfNotExists("openai", "OpenAI", "https://api.openai.com/v1/chat/completions", 
                "gpt-4o-mini", 4000, 0.7, 0.00015, 
                "OpenAI's advanced language models for educational content generation",
                "Content generation, analysis, problem-solving");

        initializeProviderIfNotExists("anthropic", "Anthropic", "https://api.anthropic.com/v1/messages", 
                "claude-3-sonnet-20240229", 4000, 0.7, 0.003, 
                "Anthropic's Claude models for safe and helpful AI assistance",
                "Safe content generation, analysis, reasoning");

        initializeProviderIfNotExists("google", "Google", "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent", 
                "gemini-pro", 4000, 0.7, 0.0005, 
                "Google's Gemini model for multimodal AI capabilities",
                "Multimodal content generation, analysis");

        initializeProviderIfNotExists("local", "Local AI", "http://host.docker.internal:11434", 
                "llama2", 4000, 0.7, 0.0, 
                "Local AI model running on Ollama - Privacy-focused, offline-capable",
                "Content generation, analysis, problem-solving (offline)");
    }

    private void initializeProviderIfNotExists(String providerName, String displayName, String apiUrl, 
                                             String defaultModel, Integer maxTokens, Double temperature, 
                                             Double costPer1kTokens, String description, String useCases) {
        if (!aiProviderConfigRepository.existsByProviderName(providerName)) {
            AiProviderConfig config = new AiProviderConfig();
            config.setProviderName(providerName);
            config.setDisplayName(displayName);
            config.setApiUrl(apiUrl);
            config.setDefaultModel(defaultModel);
            config.setMaxTokens(maxTokens);
            config.setTemperature(temperature);
            config.setCostPer1kTokens(costPer1kTokens);
            config.setDescription(description);
            config.setUseCases(useCases);
            config.setIsEnabled(true);
            
            // Set API key for local provider (no key needed)
            if ("local".equals(providerName)) {
                config.setApiKey("local-no-key-needed");
            }
            
            aiProviderConfigRepository.save(config);
        }
    }

    private AiProviderConfigDTO convertToDTO(AiProviderConfig config) {
        AiProviderConfigDTO dto = new AiProviderConfigDTO();
        dto.setId(config.getId());
        dto.setProviderName(config.getProviderName());
        dto.setDisplayName(config.getDisplayName());
        dto.setApiUrl(config.getApiUrl());
        dto.setDefaultModel(config.getDefaultModel());
        dto.setIsEnabled(config.getIsEnabled());
        dto.setMaxTokens(config.getMaxTokens());
        dto.setTemperature(config.getTemperature());
        dto.setCostPer1kTokens(config.getCostPer1kTokens());
        dto.setDescription(config.getDescription());
        dto.setUseCases(config.getUseCases());
        dto.setConfigurationJson(config.getConfigurationJson());
        dto.setCreatedAt(config.getCreatedAt());
        dto.setUpdatedAt(config.getUpdatedAt());
        dto.setHasApiKey(config.hasApiKey());
        dto.setIsConfigured(config.isConfigured());
        return dto;
    }

    private AiProviderConfig convertToEntity(AiProviderConfigDTO dto) {
        AiProviderConfig config = new AiProviderConfig();
        config.setId(dto.getId());
        config.setProviderName(dto.getProviderName());
        config.setDisplayName(dto.getDisplayName());
        config.setApiKey(dto.getApiKey());
        config.setApiUrl(dto.getApiUrl());
        config.setDefaultModel(dto.getDefaultModel());
        config.setIsEnabled(dto.getIsEnabled());
        config.setMaxTokens(dto.getMaxTokens());
        config.setTemperature(dto.getTemperature());
        config.setCostPer1kTokens(dto.getCostPer1kTokens());
        config.setDescription(dto.getDescription());
        config.setUseCases(dto.getUseCases());
        config.setConfigurationJson(dto.getConfigurationJson());
        return config;
    }
}
