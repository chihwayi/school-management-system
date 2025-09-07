package com.devtech.school_management_system.dto;

import java.time.LocalDateTime;

public class AiProviderConfigDTO {
    private Long id;
    private String providerName;
    private String displayName;
    private String apiKey;
    private String apiUrl;
    private String defaultModel;
    private Boolean isEnabled;
    private Integer maxTokens;
    private Double temperature;
    private Double costPer1kTokens;
    private String description;
    private String useCases;
    private String configurationJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean hasApiKey;
    private Boolean isConfigured;

    // Constructors
    public AiProviderConfigDTO() {}

    public AiProviderConfigDTO(Long id, String providerName, String displayName, String apiUrl, 
                              String defaultModel, Boolean isEnabled, Integer maxTokens, 
                              Double temperature, Double costPer1kTokens, String description, 
                              String useCases, String configurationJson, LocalDateTime createdAt, 
                              LocalDateTime updatedAt, Boolean hasApiKey, Boolean isConfigured) {
        this.id = id;
        this.providerName = providerName;
        this.displayName = displayName;
        this.apiUrl = apiUrl;
        this.defaultModel = defaultModel;
        this.isEnabled = isEnabled;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.costPer1kTokens = costPer1kTokens;
        this.description = description;
        this.useCases = useCases;
        this.configurationJson = configurationJson;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.hasApiKey = hasApiKey;
        this.isConfigured = isConfigured;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public String getDefaultModel() { return defaultModel; }
    public void setDefaultModel(String defaultModel) { this.defaultModel = defaultModel; }

    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getCostPer1kTokens() { return costPer1kTokens; }
    public void setCostPer1kTokens(Double costPer1kTokens) { this.costPer1kTokens = costPer1kTokens; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUseCases() { return useCases; }
    public void setUseCases(String useCases) { this.useCases = useCases; }

    public String getConfigurationJson() { return configurationJson; }
    public void setConfigurationJson(String configurationJson) { this.configurationJson = configurationJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getHasApiKey() { return hasApiKey; }
    public void setHasApiKey(Boolean hasApiKey) { this.hasApiKey = hasApiKey; }

    public Boolean getIsConfigured() { return isConfigured; }
    public void setIsConfigured(Boolean isConfigured) { this.isConfigured = isConfigured; }
}
