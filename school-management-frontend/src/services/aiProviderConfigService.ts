import api from './api';

export interface AiProviderConfig {
  id?: number;
  providerName: string;
  displayName: string;
  apiKey?: string;
  apiUrl?: string;
  defaultModel?: string;
  isEnabled: boolean;
  maxTokens?: number;
  temperature?: number;
  costPer1kTokens?: number;
  description?: string;
  useCases?: string;
  configurationJson?: string;
  createdAt?: string;
  updatedAt?: string;
  hasApiKey?: boolean;
  isConfigured?: boolean;
}

export interface AiProviderConfigCreateRequest {
  providerName: string;
  displayName: string;
  apiKey?: string;
  apiUrl?: string;
  defaultModel?: string;
  isEnabled?: boolean;
  maxTokens?: number;
  temperature?: number;
  costPer1kTokens?: number;
  description?: string;
  useCases?: string;
  configurationJson?: string;
}

export interface AiProviderConfigUpdateRequest extends AiProviderConfigCreateRequest {
  id: number;
}

export const aiProviderConfigService = {
  // Get all provider configurations
  getAllProviderConfigs: async (): Promise<AiProviderConfig[]> => {
    const response = await api.get('/admin/ai-providers');
    return response.data;
  },

  // Get only configured providers
  getConfiguredProviders: async (): Promise<AiProviderConfig[]> => {
    const response = await api.get('/admin/ai-providers/configured');
    return response.data;
  },

  // Get only enabled providers
  getEnabledProviders: async (): Promise<AiProviderConfig[]> => {
    const response = await api.get('/admin/ai-providers/enabled');
    return response.data;
  },

  // Get provider configuration by ID
  getProviderConfig: async (id: number): Promise<AiProviderConfig> => {
    const response = await api.get(`/admin/ai-providers/${id}`);
    return response.data;
  },

  // Get provider configuration by name
  getProviderConfigByName: async (providerName: string): Promise<AiProviderConfig> => {
    const response = await api.get(`/admin/ai-providers/name/${providerName}`);
    return response.data;
  },

  // Create new provider configuration
  createProviderConfig: async (config: AiProviderConfigCreateRequest): Promise<AiProviderConfig> => {
    const response = await api.post('/admin/ai-providers', config);
    return response.data;
  },

  // Update existing provider configuration
  updateProviderConfig: async (id: number, config: AiProviderConfigUpdateRequest): Promise<AiProviderConfig> => {
    const response = await api.put(`/admin/ai-providers/${id}`, config);
    return response.data;
  },

  // Delete provider configuration
  deleteProviderConfig: async (id: number): Promise<void> => {
    await api.delete(`/admin/ai-providers/${id}`);
  },

  // Toggle provider status (enable/disable)
  toggleProviderStatus: async (id: number): Promise<void> => {
    await api.post(`/admin/ai-providers/${id}/toggle`);
  },

  // Initialize default providers
  initializeDefaultProviders: async (): Promise<void> => {
    await api.post('/admin/ai-providers/initialize');
  },

  // Check if provider is configured
  isProviderConfigured: async (providerName: string): Promise<boolean> => {
    const response = await api.get(`/admin/ai-providers/${providerName}/configured`);
    return response.data.configured;
  }
};
