import axios from 'axios';
import { clearAuthState, getAccessToken } from './auth';

const API_BASE_URL = import.meta.env.VITE_API_URL ?? import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const api = axios.create({
  baseURL: API_BASE_URL,
});

api.interceptors.request.use((config) => {
  const token = getAccessToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const status = error?.response?.status;
    if (status === 401) {
      clearAuthState();
      if (window.location.pathname !== '/login') {
        window.location.assign('/login');
      }
    }

    return Promise.reject(error);
  },
);

export const endpoints = {
  authLogin: '/api/v1/auth/login',
  studentProfile: '/student/profile',
  studentDocumentUpload: '/student/profile/documents',
  notifications: '/notifications',
  bursaries: '/bursaries',
  subscriptionPlans: '/subscriptions/plans',
  subscribe: '/subscriptions/subscribe',
  adminHealth: '/admin/summary',
} as const;
