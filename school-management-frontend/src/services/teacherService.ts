import api from './api';

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
  subject?: {
    id: number;
    name: string;
    code: string;
  };
  subjectId?: number;
  subjectName?: string;
  subjectCode?: string;
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
    console.log('Teacher API response:', response.data);

    // Transform the nested response structure to match frontend expectations
    const transformedData = response.data.map((teacher: any) => ({
      id: teacher.id,
      firstName: teacher.firstName,
      lastName: teacher.lastName,
      employeeId: teacher.employeeId,
      user: {
        id: teacher.user?.id,
        username: teacher.user?.username,
        email: teacher.user?.email,
        enabled: teacher.user?.enabled,
        roles: teacher.user?.roles || [],
        createdAt: teacher.user?.createdAt,
        updatedAt: teacher.user?.updatedAt
      }
    }));

    console.log('Transformed teacher data:', transformedData);
    return transformedData;
  },

  getTeacherById: async (id: number): Promise<Teacher> => {
    const response = await api.get(`/teachers/${id}`);
    console.log('Single teacher API response:', response.data);

    // Transform single teacher response
    const teacher = response.data;
    return {
      id: teacher.id,
      firstName: teacher.firstName,
      lastName: teacher.lastName,
      employeeId: teacher.employeeId,
      user: {
        id: teacher.user?.id,
        username: teacher.user?.username,
        email: teacher.user?.email,
        enabled: teacher.user?.enabled,
        roles: teacher.user?.roles || [],
        createdAt: teacher.user?.createdAt,
        updatedAt: teacher.user?.updatedAt
      }
    };
  },

  createTeacher: async (teacherData: TeacherData): Promise<Teacher> => {
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
    const response = await api.get(`/teachers/${teacherId}/assignments`);
    return response.data;
  },
  
  saveTeacherAssignments: async (teacherId: number, assignments: SubjectAssignment[]): Promise<TeacherAssignment[]> => {
    try {
      console.log('Sending bulk assignment request:', {
        teacherId,
        assignments,
        url: `/teachers/${teacherId}/bulk-assignments`
      });
      
      const response = await api.post(`/teachers/${teacherId}/bulk-assignments`, { assignments });
      console.log('Bulk assignment response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error in saveTeacherAssignments:', error);
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

  getAssignedSubjects: async (): Promise<{ id: number; name: string; code: string }[]> => {
    const assignments = await teacherService.getAssignedSubjectsAndClasses();
    
    // Extract unique subjects from assignments
    const uniqueSubjects = new Map<number, { id: number; name: string; code: string }>();
    
    assignments.forEach(assignment => {
      if (assignment.subjectId && assignment.subjectName && assignment.subjectCode) {
        uniqueSubjects.set(assignment.subjectId, {
          id: assignment.subjectId,
          name: assignment.subjectName,
          code: assignment.subjectCode
        });
      }
    });
    
    return Array.from(uniqueSubjects.values());
  },

  getSupervisedClasses: async (): Promise<SupervisedClass[]> => {
    const response = await api.get('/teachers/supervised-classes');
    return response.data;
  },

  getTeacherStudentCount: async (): Promise<number> => {
    try {
      // Get teacher assignments first
      const assignments = await teacherService.getAssignedSubjectsAndClasses();
      
      if (!assignments || assignments.length === 0) {
        return 0;
      }

      // Get unique class combinations
      const uniqueClasses = new Set(assignments.map(a => `${a.form}-${a.section}`));
      
      // For each unique class, get the student count
      const studentCounts = await Promise.all(
        Array.from(uniqueClasses).map(async (classKey) => {
          const [form, section] = classKey.split('-');
          try {
            const response = await api.get(`/students/form/${form}/section/${section}`);
            return response.data.length;
          } catch (error) {
            console.error(`Error getting students for class ${classKey}:`, error);
            return 0;
          }
        })
      );

      // Sum up all student counts
      return studentCounts.reduce((sum, count) => sum + count, 0);
    } catch (error) {
      console.error('Error getting teacher student count:', error);
      return 0;
    }
  }
};