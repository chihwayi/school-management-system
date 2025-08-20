import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';
import { toast } from 'react-hot-toast';
import { getCurrentTenant } from '../utils/tenant';

// Create axios instance with tenant support
const api: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token and tenant
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    const tenant = getCurrentTenant();
    
    // Add tenant parameter to all requests
    if (tenant) {
      config.params = { ...config.params, tenant };
    }
    
    console.log('API Request:', {
      url: config.url,
      method: config.method,
      tenant,
      hasToken: !!token,
      tokenPreview: token ? `${token.substring(0, 20)}...` : 'No token',
      fullToken: token // Add full token for debugging
    });
    
    if (token) {
      // Check if token is expired
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const currentTime = Date.now() / 1000;
        if (payload.exp && payload.exp < currentTime) {
          console.log('Token expired, removing from localStorage');
          localStorage.removeItem('authToken');
          window.location.href = '/login';
          return Promise.reject(new Error('Token expired'));
        }
      } catch (error) {
        console.log('Invalid token format, removing from localStorage');
        localStorage.removeItem('authToken');
        window.location.href = '/login';
        return Promise.reject(new Error('Invalid token'));
      }
      
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    console.error('API Error:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
      method: error.config?.method
    });
    
    if (error.response?.status === 401) {
      console.log('Authentication failed, redirecting to login');
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    
    if (error.response?.status === 403) {
      console.log('Access forbidden, token might be invalid');
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    
    if (error.response?.status === 412) {
      // School setup required
      window.location.href = '/setup';
    }
    
    const message = error.response?.data?.message || 'An error occurred';
    toast.error(message);
    return Promise.reject(error);
  }
);

export default api;