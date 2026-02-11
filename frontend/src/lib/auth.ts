import type { AuthState, JwtPayload } from '../types';

export const AUTH_TOKEN_STORAGE_KEY = 'edutech.jwt';

export const saveToken = (token: string): void => {
  localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, token);
};

export const getToken = (): string | null => {
  return localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
};

export const clearToken = (): void => {
  localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
};

export const getAuthHeader = (): Record<string, string> => {
  const token = getToken();
  if (!token) {
    return {};
  }

  return { Authorization: `Bearer ${token}` };
};

const decodeJwtPayload = (token: string): JwtPayload | null => {
  const payloadSegment = token.split('.')[1];
  if (!payloadSegment) {
    return null;
  }

  try {
    const normalizedPayload = payloadSegment.replace(/-/g, '+').replace(/_/g, '/');
    const paddedPayload = normalizedPayload.padEnd(Math.ceil(normalizedPayload.length / 4) * 4, '=');
    const decoded = window.atob(paddedPayload);
    return JSON.parse(decoded) as JwtPayload;
  } catch {
    return null;
  }
};

export const getUserFromToken = (): AuthState | null => {
  const token = getToken();
  if (!token) {
    return null;
  }

  const payload = decodeJwtPayload(token);
  const role = payload?.role?.toUpperCase();
  if (!payload?.sub || (role !== 'STUDENT' && role !== 'ADMIN' && role !== 'COMPANY')) {
    return null;
  }

  if (typeof payload.exp === 'number' && payload.exp * 1000 <= Date.now()) {
    return null;
  }

  return {
    token,
    role,
    email: payload.sub,
  };
};
