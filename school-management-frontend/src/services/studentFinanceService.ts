import api from './api';

export interface StudentFinanceData {
  studentId: number;
  studentName: string;
  totalPaid: number;
  totalBalance: number;
  isFeesPaid: boolean;
  paymentHistory: PaymentRecord[];
}

export interface PaymentRecord {
  id: number;
  term: string;
  month: string;
  academicYear: string;
  amount: number;
  status: string;
  date: string;
}

export const studentFinanceService = {
  getStudentFinance: async (mobileNumber: string): Promise<StudentFinanceData> => {
    const response = await api.get('/student/finance', {
      params: { mobileNumber }
    });
    return response.data;
  }
};
