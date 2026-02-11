import axios from 'axios';
import { clearToken, getToken } from './auth';

const API_BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export const api = axios.create({
  baseURL: API_BASE_URL,
});

api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      clearToken();
      if (window.location.pathname !== '/login') {
        window.location.assign('/login');
      }
    }

    return Promise.reject(error);
  },
);

export const endpoints = {
  authLogin: '/api/v1/auth/login',
  authRegister: '/api/v1/auth/register',
  authMe: '/api/v1/auth/me',
  studentProfile: '/api/v1/students/profile',
  notifications: '/api/v1/notifications',
  bursaries: '/api/v1/bursaries/search',
  subscriptionPlans: '/api/v1/subscriptions/plans',
  subscribe: '/api/v1/subscriptions',
  adminDashboard: '/api/v1/analytics/dashboard',
} as const;
