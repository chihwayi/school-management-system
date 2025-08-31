import api from './api';

export interface StudentParentLoginRequest {
  mobileNumber: string;
  password: string;
  userType: 'STUDENT' | 'PARENT';
}

export interface FirstTimeLoginRequest {
  mobileNumber: string;
  password: string;
  userType: 'STUDENT' | 'PARENT';
}

export interface CheckFirstTimeResponse {
  isFirstTime: boolean;
  message: string;
}

export interface LoginResponse {
  token: string;
  message: string;
}

export const studentParentAuthService = {
  // Check if this is a first-time login for the given mobile number
  checkFirstTime: async (mobileNumber: string, userType: 'STUDENT' | 'PARENT'): Promise<CheckFirstTimeResponse> => {
    const response = await api.post('/auth/check-first-time', {
      mobileNumber,
      userType
    });
    return response.data;
  },

  // First-time login - create password
  firstTimeLogin: async (request: FirstTimeLoginRequest): Promise<LoginResponse> => {
    const response = await api.post('/auth/first-time-login', request);
    return response.data;
  },

  // Regular login
  login: async (request: StudentParentLoginRequest): Promise<LoginResponse> => {
    const response = await api.post('/auth/student-parent-login', request);
    return response.data;
  },

  // Get student profile
  getStudentProfile: async (studentId: number) => {
    const response = await api.get(`/auth/student/profile?studentId=${studentId}`);
    return response.data;
  },

  // Get parent profile
  getParentProfile: async (guardianId: number) => {
    const response = await api.get(`/auth/parent/profile?guardianId=${guardianId}`);
    return response.data;
  }
};

