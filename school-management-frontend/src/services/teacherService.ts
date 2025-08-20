import api from './api';
import type { TeacherRegistrationDTO } from '../types';

interface TeacherData {
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  address?: string;
  userId?: number;
}

interface Teacher extends TeacherData {
  id: number;
}

interface SubjectAssignment {
  subjectId: number;
  classGroupId: number;
}

interface TeacherAssignment {
  id: number;
  subject: {
    id: number;
    name: string;
    code: string;
  };
  form: string;
  section: string;
}

interface SupervisedClass {
  id: number;
  form: string;
  section: string;
  academicYear: string;
  studentCount?: number;
}

export const teacherService = {
  getAllTeachers: async (): Promise<Teacher[]> => {
    const response = await api.get('/teachers?includeUser=true');
    return response.data;
  },

  getTeacherById: async (id: number): Promise<Teacher> => {
    const response = await api.get(`/teachers/${id}`);
    return response.data;
  },

  createTeacher: async (teacherData: TeacherRegistrationDTO): Promise<Teacher> => {
    const response = await api.post('/teachers', teacherData);
    return response.data;
  },

  updateTeacher: async (id: number, teacherData: Partial<TeacherData>): Promise<Teacher> => {
    const response = await api.put(`/teachers/${id}`, teacherData);
    return response.data;
  },

  deleteTeacher: async (id: number): Promise<void> => {
    await api.delete(`/teachers/${id}`);
  },

  assignTeacher: async (assignmentData: SubjectAssignment): Promise<TeacherAssignment> => {
    const response = await api.post('/teachers/assign', assignmentData);
    return response.data;
  },
  
  getTeacherAssignments: async (teacherId: number): Promise<TeacherAssignment[]> => {
    console.log('teacherService.getTeacherAssignments called with teacherId:', teacherId);
    const response = await api.get(`/teachers/${teacherId}/assignments`);
    console.log('teacherService.getTeacherAssignments response:', response.data);
    return response.data;
  },
  
  saveTeacherAssignments: async (teacherId: number, assignments: SubjectAssignment[]): Promise<TeacherAssignment[]> => {
    try {
      console.log('=== TEACHER SERVICE DEBUG ===');
      console.log('Sending bulk assignment request:', {
        teacherId,
        assignments,
        url: `/teachers/${teacherId}/bulk-assignments`
      });
      
      const requestBody = { assignments };
      console.log('Request body being sent:', requestBody);
      
      const response = await api.post(`/teachers/${teacherId}/bulk-assignments`, requestBody);
      console.log('Bulk assignment response:', response.data);
      console.log('Response status:', response.status);
      console.log('=== END TEACHER SERVICE DEBUG ===');
      return response.data;
    } catch (error) {
      console.error('Error in saveTeacherAssignments:', error);
      console.log('=== END TEACHER SERVICE DEBUG ===');
      throw error;
    }
  },

  // New functions for ClassTeacherDashboard
  getCurrentTeacher: async (): Promise<Teacher> => {
    const response = await api.get('/teachers/current');
    return response.data;
  },

  getAssignedSubjectsAndClasses: async (): Promise<TeacherAssignment[]> => {
    const response = await api.get('/teachers/assignments/current');
    return response.data;
  },

  getAllTeacherAssignments: async (): Promise<TeacherAssignment[]> => {
    const response = await api.get('/teachers/assignments/all');
    return response.data;
  },

  getSupervisedClasses: async (): Promise<SupervisedClass[]> => {
    const response = await api.get('/teachers/supervised-classes');
    return response.data;
  }
};