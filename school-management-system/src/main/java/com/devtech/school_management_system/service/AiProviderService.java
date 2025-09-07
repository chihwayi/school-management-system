package com.devtech.school_management_system.service;

import com.devtech.school_management_system.entity.AiGeneratedContent;
import com.devtech.school_management_system.entity.AiProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiProviderService {
    
    @Autowired
    private AiProviderConfigService aiProviderConfigService;
    
    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;
    
    @Value("${ai.anthropic.api-key:}")
    private String anthropicApiKey;
    
    @Value("${ai.google.api-key:}")
    private String googleApiKey;
    
    @Value("${ai.azure.openai.endpoint:}")
    private String azureOpenaiEndpoint;
    
    @Value("${ai.azure.openai.api-key:}")
    private String azureOpenaiApiKey;
    
    @Value("${ai.huggingface.api-key:}")
    private String huggingfaceApiKey;
    
    @Value("${ai.local.endpoint:}")
    private String localAiEndpoint;
    
    public enum AiProvider {
        OPENAI("OpenAI", "openai"),
        ANTHROPIC("Anthropic", "anthropic"),
        GOOGLE("Google", "google"),
        AZURE_OPENAI("Azure OpenAI", "azure-openai"),
        HUGGINGFACE("Hugging Face", "huggingface"),
        LOCAL("Local AI", "local"),
        MOCK("Mock AI", "mock");
        
        private final String displayName;
        private final String configKey;
        
        AiProvider(String displayName, String configKey) {
            this.displayName = displayName;
            this.configKey = configKey;
        }
        
        public String getDisplayName() { return displayName; }
        public String getConfigKey() { return configKey; }
    }
    
    public Map<String, Object> getProviderStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // Check OpenAI
        status.put("openai", Map.of(
            "available", openaiApiKey != null && !openaiApiKey.trim().isEmpty(),
            "configured", openaiApiKey != null && !openaiApiKey.trim().isEmpty() && 
                        !openaiApiKey.equals("YOUR_OPENAI_API_KEY_HERE")
        ));
        
        // Check Anthropic
        status.put("anthropic", Map.of(
            "available", anthropicApiKey != null && !anthropicApiKey.trim().isEmpty(),
            "configured", anthropicApiKey != null && !anthropicApiKey.trim().isEmpty() && 
                        !anthropicApiKey.equals("YOUR_ANTHROPIC_API_KEY_HERE")
        ));
        
        // Check Google
        status.put("google", Map.of(
            "available", googleApiKey != null && !googleApiKey.trim().isEmpty(),
            "configured", googleApiKey != null && !googleApiKey.trim().isEmpty() && 
                        !googleApiKey.equals("YOUR_GOOGLE_API_KEY_HERE")
        ));
        
        // Check Azure OpenAI
        status.put("azure-openai", Map.of(
            "available", azureOpenaiEndpoint != null && !azureOpenaiEndpoint.trim().isEmpty() && 
                        azureOpenaiApiKey != null && !azureOpenaiApiKey.trim().isEmpty(),
            "configured", azureOpenaiEndpoint != null && !azureOpenaiEndpoint.trim().isEmpty() && 
                        azureOpenaiApiKey != null && !azureOpenaiApiKey.trim().isEmpty() &&
                        !azureOpenaiEndpoint.equals("YOUR_AZURE_ENDPOINT_HERE")
        ));
        
        // Check Hugging Face
        status.put("huggingface", Map.of(
            "available", huggingfaceApiKey != null && !huggingfaceApiKey.trim().isEmpty(),
            "configured", huggingfaceApiKey != null && !huggingfaceApiKey.trim().isEmpty() && 
                        !huggingfaceApiKey.equals("YOUR_HUGGINGFACE_API_KEY_HERE")
        ));
        
        // Check Local AI
        status.put("local", Map.of(
            "available", localAiEndpoint != null && !localAiEndpoint.trim().isEmpty(),
            "configured", localAiEndpoint != null && !localAiEndpoint.trim().isEmpty() && 
                        !localAiEndpoint.equals("YOUR_LOCAL_AI_ENDPOINT_HERE")
        ));
        
        // Mock is always available
        status.put("mock", Map.of(
            "available", true,
            "configured", true
        ));
        
        return status;
    }
    
    public String generateContent(String prompt, AiGeneratedContent.ContentType contentType, String provider, String model) {
        try {
            switch (AiProvider.valueOf(provider.toUpperCase().replace("-", "_"))) {
                case OPENAI:
                    return generateWithOpenAI(prompt, contentType, model);
                case ANTHROPIC:
                    return generateWithAnthropic(prompt, contentType, model);
                case GOOGLE:
                    return generateWithGoogle(prompt, contentType, model);
                case AZURE_OPENAI:
                    return generateWithAzureOpenAI(prompt, contentType, model);
                case HUGGINGFACE:
                    return generateWithHuggingFace(prompt, contentType, model);
                case LOCAL:
                    return generateWithLocalAI(prompt, contentType, model);
                case MOCK:
                default:
                    return generateMockContent(prompt, contentType);
            }
        } catch (Exception e) {
            System.err.println("Error generating content with " + provider + ": " + e.getMessage());
            return generateMockContent(prompt, contentType);
        }
    }
    
    private String generateWithOpenAI(String prompt, AiGeneratedContent.ContentType contentType, String model) {
        // Implementation for OpenAI (existing logic)
        return "OpenAI generation not implemented yet";
    }
    
    private String generateWithAnthropic(String prompt, AiGeneratedContent.ContentType contentType, String model) {
        // Implementation for Anthropic Claude
        return "Anthropic Claude generation not implemented yet";
    }
    
    private String generateWithGoogle(String prompt, AiGeneratedContent.ContentType contentType, String model) {
        // Implementation for Google Gemini
        return "Google Gemini generation not implemented yet";
    }
    
    private String generateWithAzureOpenAI(String prompt, AiGeneratedContent.ContentType contentType, String model) {
        // Implementation for Azure OpenAI
        return "Azure OpenAI generation not implemented yet";
    }
    
    private String generateWithHuggingFace(String prompt, AiGeneratedContent.ContentType contentType, String model) {
        // Implementation for Hugging Face models
        return "Hugging Face generation not implemented yet";
    }
    
    private String generateWithLocalAI(String prompt, AiGeneratedContent.ContentType contentType, String model) {
        try {
            System.out.println("Calling local AI (Ollama) with model: " + model);
            System.out.println("Ollama endpoint: " + localAiEndpoint);
            
            // Build the request for Ollama API using proper JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.node.ObjectNode requestJson = mapper.createObjectNode();
            requestJson.put("model", model);
            requestJson.put("prompt", prompt);
            requestJson.put("stream", false);
            
            String requestBody = mapper.writeValueAsString(requestJson);
            System.out.println("Request body: " + requestBody);
            
            // Make HTTP request to Ollama
            java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(30))
                .build();
                
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(localAiEndpoint + "/api/generate"))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofSeconds(300)) // 5 minutes for AI generation
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            System.out.println("Sending request to: " + localAiEndpoint + "/api/generate");
            System.out.println("Request started at: " + java.time.LocalDateTime.now());
            
            java.net.http.HttpResponse<String> response = client.send(request, 
                java.net.http.HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Request completed at: " + java.time.LocalDateTime.now());
            System.out.println("Response status: " + response.statusCode());
            System.out.println("Response body length: " + (response.body() != null ? response.body().length() : 0));
            
            if (response.statusCode() == 200) {
                // Parse the JSON response
                com.fasterxml.jackson.databind.JsonNode jsonResponse = mapper.readTree(response.body());
                String generatedText = jsonResponse.get("response").asText();
                
                System.out.println("Local AI (Ollama) response received successfully");
                return generatedText;
            } else {
                System.err.println("Ollama API error: " + response.statusCode() + " - " + response.body());
                return "Local AI generation failed: " + response.statusCode();
            }
            
        } catch (Exception e) {
            System.err.println("Error calling local AI (Ollama): " + e.getMessage());
            e.printStackTrace();
            return "Local AI generation error: " + e.getMessage();
        }
    }
    
    private String generateMockContent(String prompt, AiGeneratedContent.ContentType contentType) {
        return "Mock Generated " + contentType + ":\n\n" +
               "Context: " + prompt + "\n\n" +
               "This is a sample generated content. In a real implementation, this would be " +
               "generated by an AI model based on the uploaded resources and specifications.\n\n" +
               "Content Type: " + contentType + "\n" +
               "Generated at: " + java.time.LocalDateTime.now() + "\n" +
               "Model: Mock AI Service";
    }
    
    /**
     * Generate content using stored provider configuration
     */
    public String generateContentWithConfig(String prompt, AiGeneratedContent.ContentType contentType, 
                                          String provider, String model) {
        try {
            // Get the stored configuration for the provider
            var providerConfig = aiProviderConfigService.getConfiguredProvider(provider);
            
            if (providerConfig.isEmpty()) {
                System.err.println("Provider " + provider + " is not configured or enabled");
                return generateMockContent(prompt, contentType);
            }
            
            AiProviderConfig config = providerConfig.get();
            
            // Use the stored configuration to generate content
            return generateContentWithStoredConfig(prompt, contentType, config, model);
            
        } catch (Exception e) {
            System.err.println("Error generating content with stored config: " + e.getMessage());
            return generateMockContent(prompt, contentType);
        }
    }
    
    private String generateContentWithStoredConfig(String prompt, AiGeneratedContent.ContentType contentType, 
                                                 AiProviderConfig config, String model) {
        String providerName = config.getProviderName();
        
        if ("openai".equals(providerName)) {
            return generateWithOpenAI(prompt, contentType, config.getApiKey(), config.getApiUrl(), model);
        } else if ("anthropic".equals(providerName)) {
            return generateWithAnthropic(prompt, contentType, config.getApiKey(), config.getApiUrl(), model);
        } else if ("google".equals(providerName)) {
            return generateWithGoogle(prompt, contentType, config.getApiKey(), config.getApiUrl(), model);
        } else if ("local".equals(providerName)) {
            return generateWithLocalAI(prompt, contentType, config.getApiUrl());
        } else {
            System.err.println("Unsupported provider: " + providerName);
            return generateMockContent(prompt, contentType);
        }
    }
    
    private String generateWithOpenAI(String prompt, AiGeneratedContent.ContentType contentType, 
                                    String apiKey, String apiUrl, String model) {
        // Implementation for OpenAI with stored config
        // This would use the stored API key and URL
        System.out.println("Generating with OpenAI using stored config - Model: " + model);
        return generateMockContent(prompt, contentType);
    }
    
    private String generateWithAnthropic(String prompt, AiGeneratedContent.ContentType contentType, 
                                       String apiKey, String apiUrl, String model) {
        // Implementation for Anthropic with stored config
        System.out.println("Generating with Anthropic using stored config - Model: " + model);
        return generateMockContent(prompt, contentType);
    }
    
    private String generateWithGoogle(String prompt, AiGeneratedContent.ContentType contentType, 
                                    String apiKey, String apiUrl, String model) {
        // Implementation for Google with stored config
        System.out.println("Generating with Google using stored config - Model: " + model);
        return generateMockContent(prompt, contentType);
    }
}
