import api from './api';

export interface StudentReport {
  id: number;
  studentId: number;
  studentName: string;
  form: string;
  section: string;
  term: string;
  academicYear: string;
  overallComment: string;
  finalized: boolean;
  classTeacherSignatureUrl?: string;
  subjectReports: StudentSubjectReport[];
  createdAt?: string;
  updatedAt?: string;
}

export interface StudentSubjectReport {
  id: number;
  subjectId: number;
  subjectName: string;
  subjectCode: string;
  courseworkMark?: number;
  examMark?: number;
  finalMark?: number;
  comment: string;
  teacherSignatureUrl?: string;
  assessments?: StudentAssessment[];
}

export interface StudentAssessment {
  id: number;
  type: string;
  title: string;
  date: string;
  score: number;
  maxScore: number;
  percentage: number;
}

export interface StudentFinance {
  studentId: number;
  totalPaid: number;
  totalBalance: number;
  isFeesPaid: boolean;
  paymentHistory: StudentPayment[];
}

export interface StudentPayment {
  id: number;
  term: string;
  month: string;
  academicYear: string;
  monthlyFeeAmount: number;
  amountPaid: number;
  balance: number;
  paymentDate: string;
  paymentStatus: string;
}

export const studentService = {
  // Get all students
  async getAllStudents(): Promise<any[]> {
    const response = await api.get('/student/all');
    return response.data;
  },

  // Get student by ID
  async getStudentById(id: number): Promise<any> {
    const response = await api.get(`/students/${id}`);
    return response.data;
  },

  // Get student reports (only finalized reports if fees are paid)
  async getStudentReports(studentId: number): Promise<StudentReport[]> {
    const response = await api.get(`/student/reports/${studentId}`);
    return response.data;
  },

  // Get student finance status
  async getStudentFinance(studentId: number): Promise<StudentFinance> {
    const response = await api.get(`/student/finance/${studentId}`);
    return response.data;
  },

  // Get report PDF URL
  async getReportPdf(reportId: number): Promise<string> {
    const response = await api.get(`/reports/${reportId}/pdf`);
    return response.data;
  },

  // Get student profile by mobile number
  async getStudentProfile(mobileNumber: string): Promise<any> {
    const response = await api.get(`/student/profile?mobileNumber=${encodeURIComponent(mobileNumber)}`);
    return response.data;
  },

  // Get student reports by mobile number
  async getStudentReportsByMobile(mobileNumber: string): Promise<StudentReport[]> {
    const response = await api.get(`/student/reports?mobileNumber=${encodeURIComponent(mobileNumber)}`);
    return response.data;
  },

  // Get student finance by mobile number
  async getStudentFinanceByMobile(mobileNumber: string): Promise<StudentFinance> {
    const response = await api.get(`/student/finance?mobileNumber=${encodeURIComponent(mobileNumber)}`);
    return response.data;
  }
};