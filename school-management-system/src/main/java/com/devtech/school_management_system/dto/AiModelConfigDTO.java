package com.devtech.school_management_system.dto;

public class AiModelConfigDTO {
    private String modelId;
    private String modelName;
    private String provider;
    private String description;
    private boolean available;
    private Double costPerToken;
    private Integer maxTokens;
    private String capabilities;

    // Constructors
    public AiModelConfigDTO() {}

    public AiModelConfigDTO(String modelId, String modelName, String provider, String description, 
                           boolean available, Double costPerToken, Integer maxTokens, String capabilities) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.provider = provider;
        this.description = description;
        this.available = available;
        this.costPerToken = costPerToken;
        this.maxTokens = maxTokens;
        this.capabilities = capabilities;
    }

    // Getters and Setters
    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Double getCostPerToken() { return costPerToken; }
    public void setCostPerToken(Double costPerToken) { this.costPerToken = costPerToken; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public String getCapabilities() { return capabilities; }
    public void setCapabilities(String capabilities) { this.capabilities = capabilities; }
}
