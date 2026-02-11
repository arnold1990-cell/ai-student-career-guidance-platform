import type { AuthState, JwtPayload } from '../types';

export const AUTH_STORAGE_KEY = 'edurite-auth';

export const loadAuthState = (): AuthState | null => {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    const parsed = JSON.parse(raw) as AuthState;
    if (!parsed.accessToken || !parsed.refreshToken || !parsed.role) {
      return null;
    }
    return parsed;
  } catch {
    return null;
  }
};

export const parseJwtPayload = (token: string): JwtPayload | null => {
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
