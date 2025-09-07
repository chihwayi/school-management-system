import api from './api';
// Temporarily use any types to bypass the import issue
// import { 
//   AiResource, 
//   AiGeneratedContent, 
//   AiContentGenerationRequest, 
//   AiUsageStats, 
//   AiMonthlyUsage,
//   ResourceType,
//   ContentType,
//   DifficultyLevel
// } from '../types/ai';

export const aiService = {
  // AI Resource Management
  uploadResource: async (
    file: File,
    type: any,
    subjectId: number,
    title: string,
    description?: string,
    academicYear?: string,
    formLevel?: string
  ): Promise<any> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);
    formData.append('subjectId', subjectId.toString());
    formData.append('title', title);
    if (description) formData.append('description', description);
    if (academicYear) formData.append('academicYear', academicYear);
    if (formLevel) formData.append('formLevel', formLevel);

    const response = await api.post('/ai/resources/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  getTeacherResources: async (): Promise<any[]> => {
    const response = await api.get('/ai/resources/teacher');
    return response.data;
  },

  getProcessedResourcesBySubject: async (subjectId: number): Promise<any[]> => {
    const response = await api.get(`/ai/resources/subject/${subjectId}`);
    return response.data;
  },

  // AI Content Generation
  generateContent: async (request: any): Promise<any> => {
    const response = await api.post('/ai/content/generate', request);
    return response.data;
  },

  getTeacherGeneratedContent: async (): Promise<any[]> => {
    const response = await api.get('/ai/content/teacher');
    return response.data;
  },

  getPublishedContentBySubject: async (subjectId: number): Promise<any[]> => {
    const response = await api.get(`/ai/content/published/subject/${subjectId}`);
    return response.data;
  },

  getPublishedContentForStudents: async (): Promise<any[]> => {
    const response = await api.get('/ai/content/published/students');
    return response.data;
  },

  publishContent: async (contentId: number): Promise<any> => {
    const response = await api.post(`/ai/content/${contentId}/publish`);
    return response.data;
  },

  useContent: async (contentId: number): Promise<{ message: string }> => {
    const response = await api.post(`/ai/content/${contentId}/use`);
    return response.data;
  },

  // AI Usage Analytics
  getTeacherUsageStats: async (): Promise<any> => {
    const response = await api.get('/ai/analytics/usage');
    return response.data;
  },

  getMonthlyUsageStats: async (): Promise<any> => {
    const response = await api.get('/ai/analytics/usage/monthly');
    return response.data;
  },

  getUsageLimits: async (): Promise<any> => {
    const response = await api.get('/ai/analytics/usage/limits');
    return response.data;
  },

  // AI Provider Management
  getProviderStatus: async (): Promise<any> => {
    const response = await api.get('/ai/providers/status');
    return response.data;
  },

  getAvailableModels: async (): Promise<any[]> => {
    const response = await api.get('/ai/providers/models');
    return response.data;
  },

    selectProvider: async (provider: string, model: string): Promise<any> => {
        const response = await api.post('/ai/providers/select', null, {
            params: { provider, model }
        });
        return response.data;
    },

    deleteUnpublishedContent: async (): Promise<any> => {
        const response = await api.delete('/ai/content/unpublished');
        return response.data;
    },

  // AI Configuration
  getResourceTypes: async (): Promise<any[]> => {
    const response = await api.get('/ai/resource-types');
    return response.data;
  },

  getContentTypes: async (): Promise<any[]> => {
    const response = await api.get('/ai/content-types');
    return response.data;
  },

  getDifficultyLevels: async (): Promise<any[]> => {
    const response = await api.get('/ai/difficulty-levels');
    return response.data;
  },

  // Resource Management
  updateResource: async (resourceId: number, title: string, description: string, academicYear: string, formLevel: string): Promise<any> => {
    const response = await api.put(`/ai/resources/${resourceId}`, {
      title,
      description,
      academicYear,
      formLevel
    });
    return response.data;
  },

  deleteResource: async (resourceId: number): Promise<void> => {
    await api.delete(`/ai/resources/${resourceId}`);
  },

  downloadResource: async (resourceId: number): Promise<void> => {
    const response = await api.get(`/ai/resources/${resourceId}/download`, {
      responseType: 'blob'
    });
    
    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'resource'); // You might want to get the filename from response headers
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },

  // Content Management
  updateContent: async (contentId: number, title: string, description: string, published: boolean): Promise<any> => {
    const response = await api.put(`/ai/content/${contentId}`, {
      title,
      description,
      published
    });
    return response.data;
  },

  deleteContent: async (contentId: number): Promise<void> => {
    await api.delete(`/ai/content/${contentId}`);
  },

  exportContent: async (contentId: number, format: string): Promise<void> => {
    const response = await api.get(`/ai/content/${contentId}/export`, {
      params: { format },
      responseType: 'blob'
    });
    
    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `content-${contentId}.${format}`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },

  // Health Check
  healthCheck: async (): Promise<{ status: string; service: string; timestamp: string }> => {
    const response = await api.get('/ai/health');
    return response.data;
  },

  // AI Model Configuration (Legacy - keeping for compatibility)
  selectModel: async (modelId: string): Promise<any> => {
    const response = await api.post(`/ai/models/${modelId}/select`);
    return response.data;
  },

  getCurrentModel: async (): Promise<any> => {
    const response = await api.get('/ai/models/current');
    return response.data;
  },

  getCurrentProvider: async (): Promise<{ provider: string; model: string }> => {
    const response = await api.get('/ai/models/current');
    return {
      provider: response.data.provider || 'openai',
      model: response.data.modelId || 'gpt-4o-mini'
    };
  },

  // AI Templates
  getTemplates: async (): Promise<any[]> => {
    const response = await api.get('/ai/templates');
    return response.data;
  },

  getPublicTemplates: async (): Promise<any[]> => {
    const response = await api.get('/ai/templates/public');
    return response.data;
  },

  createTemplate: async (template: any): Promise<any> => {
    const response = await api.post('/ai/templates', template);
    return response.data;
  },

  updateTemplate: async (templateId: number, template: any): Promise<any> => {
    const response = await api.put(`/ai/templates/${templateId}`, template);
    return response.data;
  },

  deleteTemplate: async (templateId: number): Promise<void> => {
    await api.delete(`/ai/templates/${templateId}`);
  },

  useTemplate: async (templateId: number): Promise<any> => {
    const response = await api.post(`/ai/templates/${templateId}/use`);
    return response.data;
  },

  // WhatsApp Sharing
  shareContentToClass: async (contentId: number, form: string, section: string): Promise<void> => {
    await api.post(`/ai/content/${contentId}/share-whatsapp/class`, null, {
      params: { form, section }
    });
  },

  shareContentToMultipleClasses: async (contentId: number, classList: string[]): Promise<void> => {
    await api.post(`/ai/content/${contentId}/share-whatsapp/classes`, classList);
  },

  shareContentToSpecificStudents: async (contentId: number, studentIds: number[]): Promise<void> => {
    await api.post(`/ai/content/${contentId}/share-whatsapp/students`, studentIds);
  },

  getWhatsAppStats: async (form: string, section: string, academicYear: string): Promise<any> => {
    const response = await api.get(`/ai/whatsapp/stats/${form}/${section}`, {
      params: { academicYear }
    });
    return response.data;
  },

  getStudentsWithWhatsApp: async (form: string, section: string, academicYear: string): Promise<any[]> => {
    const response = await api.get(`/ai/whatsapp/students/${form}/${section}`, {
      params: { academicYear }
    });
    return response.data;
  }
};
