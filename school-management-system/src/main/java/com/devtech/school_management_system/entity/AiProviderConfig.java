package com.devtech.school_management_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_provider_configs")
public class AiProviderConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_name", nullable = false, unique = true)
    private String providerName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "api_key", nullable = true)
    @JsonIgnore // Never expose API keys in JSON responses
    private String apiKey;

    @Column(name = "api_url", nullable = true)
    private String apiUrl;

    @Column(name = "default_model", nullable = true)
    private String defaultModel;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Column(name = "max_tokens", nullable = true)
    private Integer maxTokens;

    @Column(name = "temperature", nullable = true)
    private Double temperature;

    @Column(name = "cost_per_1k_tokens", nullable = true)
    private Double costPer1kTokens;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "use_cases", columnDefinition = "TEXT")
    private String useCases;

    @Column(name = "configuration_json", columnDefinition = "TEXT")
    private String configurationJson;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public AiProviderConfig() {}

    public AiProviderConfig(String providerName, String displayName, String apiKey, String apiUrl) {
        this.providerName = providerName;
        this.displayName = displayName;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
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

    // Helper methods
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    public boolean isConfigured() {
        return hasApiKey() && isEnabled;
    }
}
