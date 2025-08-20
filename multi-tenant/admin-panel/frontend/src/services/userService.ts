import api from './api';

export interface User {
  id: string;
  username: string;
  email: string;
  role: 'SUPER_ADMIN' | 'ADMIN' | 'MANAGER';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  lastLogin: string | null;
  createdAt: string;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  role: 'SUPER_ADMIN' | 'ADMIN' | 'MANAGER' | 'SCHOOL_ADMIN' | 'TEACHER' | 'CLERK';
  schoolId?: string;
}

const userService = {
  getAllUsers: async (): Promise<User[]> => {
    const response = await api.get('/admin/schools/users');
    return response.data;
  },

  createUser: async (request: CreateUserRequest): Promise<User> => {
    const response = await api.post('/admin/schools/users', request);
    return response.data;
  },

  deleteUser: async (userId: string): Promise<void> => {
    await api.delete(`/admin/schools/users/${userId}`);
  },

  suspendUser: async (userId: string): Promise<User> => {
    const response = await api.post(`/admin/schools/users/${userId}/suspend`);
    return response.data;
  },

  activateUser: async (userId: string): Promise<User> => {
    const response = await api.post(`/admin/schools/users/${userId}/activate`);
    return response.data;
  }
};

export default userService;