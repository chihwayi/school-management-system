import api from './api';

export interface School {
  schoolId: string;
  name: string;
  subdomain: string;
  status: string;
  planType: string;
  adminEmail: string;
  createdAt: string;
}

const schoolService = {
  getAllSchools: async (page = 0, size = 10): Promise<School[]> => {
    const response = await api.get(`/admin/schools?page=${page}&size=${size}`);
    return response.data;
  },

  createSchool: async (schoolData: any): Promise<School> => {
    const response = await api.post('/admin/schools', schoolData);
    return response.data;
  },

  suspendSchool: async (schoolId: string, reason: string): Promise<void> => {
    await api.post(`/admin/schools/${schoolId}/suspend`, null, {
      params: { reason }
    });
  },

  activateSchool: async (schoolId: string): Promise<void> => {
    await api.post(`/admin/schools/${schoolId}/activate`);
  },

  deleteSchool: async (schoolId: string): Promise<void> => {
    await api.delete(`/admin/schools/${schoolId}`);
  },

  getSchoolStats: async (schoolId: string): Promise<any> => {
    const response = await api.get(`/admin/schools/${schoolId}/stats`);
    return response.data;
  }
};

export default schoolService;