import api from './api';

export interface ParentReport {
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
  subjectReports: ParentSubjectReport[];
  createdAt?: string;
  updatedAt?: string;
}

export interface ParentSubjectReport {
  id: number;
  subjectId: number;
  subjectName: string;
  subjectCode: string;
  courseworkMark?: number;
  examMark?: number;
  finalMark?: number;
  comment: string;
  teacherSignatureUrl?: string;
  assessments?: ParentAssessment[];
}

export interface ParentAssessment {
  id: number;
  type: string;
  title: string;
  score: number;
  maxScore: number;
  percentage: number;
  date: string;
}

export interface ParentAttendanceSummary {
  studentId: number;
  studentName: string;
  totalDays: number;
  presentDays: number;
  absentDays: number;
  attendancePercentage: number;
  monthlyBreakdown: MonthlyAttendance[];
  recentAttendance: RecentAttendance[];
}

export interface MonthlyAttendance {
  month: string;
  year: number;
  totalDays: number;
  presentDays: number;
  absentDays: number;
  percentage: number;
}

export interface RecentAttendance {
  date: string;
  present: boolean;
  markedBy?: string;
}

export interface StudentFinanceStatus {
  studentId: number;
  studentName: string;
  totalPaid: number;
  totalBalance: number;
  isFeesPaid: boolean;
  paymentHistory: PaymentHistory[];
}

export interface PaymentHistory {
  id: number;
  amount: number;
  date: string;
  month: string;
  term: string;
  academicYear: string;
  status: string;
}

export const parentService = {
  // Get all reports for a student (only if fees are paid)
  getStudentReports: async (studentId: number): Promise<ParentReport[]> => {
    const response = await api.get(`/parent/reports/student/${studentId}`);
    return response.data;
  },

  // Get a specific report with full details
  getReportDetails: async (reportId: number): Promise<ParentReport> => {
    const response = await api.get(`/parent/reports/${reportId}`);
    return response.data;
  },

  // Get report PDF URL for viewing
  getReportPdf: async (reportId: number): Promise<string> => {
    const response = await api.get(`/parent/reports/${reportId}/pdf`);
    return response.data.pdfUrl;
  },

  // Get attendance summary for a student
  getStudentAttendanceSummary: async (studentId: number): Promise<ParentAttendanceSummary> => {
    const response = await api.get(`/parent/attendance/student/${studentId}`);
    return response.data;
  },

  // Get detailed attendance records for a student
  getStudentAttendance: async (studentId: number): Promise<any[]> => {
    const response = await api.get(`/parent/attendance/student/${studentId}/records`);
    return response.data;
  },

  // Get monthly attendance breakdown
  getMonthlyAttendance: async (studentId: number, year: number): Promise<MonthlyAttendance[]> => {
    const response = await api.get(`/parent/attendance/student/${studentId}/monthly/${year}`);
    return response.data;
  },

  // Get recent attendance records
  getRecentAttendance: async (studentId: number, days: number = 30): Promise<RecentAttendance[]> => {
    const response = await api.get(`/parent/attendance/student/${studentId}/recent/${days}`);
    return response.data;
  },

  // Check if student fees are fully paid
  checkFeesStatus: async (studentId: number): Promise<StudentFinanceStatus> => {
    const response = await api.get(`/parent/finance/student/${studentId}`);
    return response.data;
  },

  // Get all children for a parent
  getChildren: async (mobileNumber: string): Promise<any[]> => {
    const response = await api.get(`/parent/children?mobileNumber=${encodeURIComponent(mobileNumber)}`);
    return response.data;
  },

  // Get parent profile
  getProfile: async (mobileNumber: string): Promise<any> => {
    const response = await api.get(`/parent/profile?mobileNumber=${encodeURIComponent(mobileNumber)}`);
    return response.data;
  }
};
