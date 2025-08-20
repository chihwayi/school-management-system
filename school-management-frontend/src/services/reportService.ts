import api from './api';

interface StudentReport {
  id: number;
  studentId: number;
  studentName: string;
  form: string;
  section: string;
  term: string;
  academicYear: string;
  subjectReports: SubjectReport[];
  overallComment?: string;
  finalized: boolean;
}

interface SubjectReport {
  id: number;
  subjectId: number;
  subjectName: string;
  subjectCode: string;
  courseworkMark?: number;
  examMark?: number;
  finalMark: number;
  courseworkGrade?: string;
  examGrade?: string;
  comment?: string;
  teacherId?: number;
  teacherName?: string;
}

interface SubjectCommentDTO {
  reportId: number;
  subjectId: number;
  comment: string;
}

interface OverallCommentDTO {
  reportId: number;
  comment: string;
}

// Export interfaces
export type { StudentReport, SubjectReport, SubjectCommentDTO, OverallCommentDTO };

export const reportService = {
  // Get reports for class teacher's supervised classes
  getClassReports: async (form: string, section: string, term: string, year: string): Promise<StudentReport[]> => {
    const response = await api.get(`/reports/class/${encodeURIComponent(form)}/${encodeURIComponent(section)}/${encodeURIComponent(term)}/${encodeURIComponent(year)}`);
    return response.data;
  },

  // Get reports for teacher's assigned subjects
  getSubjectReports: async (subjectId: number, form: string, section: string, term: string, year: string): Promise<StudentReport[]> => {
    const response = await api.get(`/reports/subject/${subjectId}/${encodeURIComponent(form)}/${encodeURIComponent(section)}/${encodeURIComponent(term)}/${encodeURIComponent(year)}`);
    return response.data;
  },

  // Add subject comment
  addSubjectComment: async (commentData: SubjectCommentDTO): Promise<void> => {
    await api.post('/reports/subject-comment', commentData);
  },

  // Add overall comment (class teacher only)
  addOverallComment: async (commentData: OverallCommentDTO): Promise<void> => {
    await api.post('/reports/overall-comment', commentData);
  },

  // Finalize report (class teacher only)
  finalizeReport: async (reportId: number): Promise<void> => {
    await api.post(`/reports/${reportId}/finalize`);
  },

  /**
   * Update attendance statistics for a specific report
   */
  updateReportAttendance: async (reportId: number, term: string, academicYear: string): Promise<void> => {
    await api.post(`/reports/${reportId}/attendance`, null, {
      params: { term, academicYear }
    });
  },

  /**
   * Update attendance statistics for all reports in a class
   */
  updateClassAttendance: async (form: string, section: string, term: string, academicYear: string): Promise<void> => {
    await api.post(`/reports/class/${encodeURIComponent(form)}/${encodeURIComponent(section)}/attendance`, null, {
      params: { term, academicYear }
    });
  },

  // Generate report for student
  generateStudentReport: async (studentId: number, term: string, year: string): Promise<StudentReport> => {
    const response = await api.post('/reports/generate', { studentId, term, year });
    return response.data;
  },

  // Get all reports for a student
  getStudentReports: async (studentId: number): Promise<StudentReport[]> => {
    const response = await api.get(`/reports/student/${studentId}`);
    return response.data;
  },

  // Get a specific report by ID
  getReportById: async (reportId: number): Promise<StudentReport> => {
    const response = await api.get(`/reports/${reportId}`);
    return response.data;
  }
};