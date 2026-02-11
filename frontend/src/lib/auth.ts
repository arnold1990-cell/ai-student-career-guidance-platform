import type { AuthState, JwtPayload } from '../types';

export const AUTH_STORAGE_KEY = 'edurite-auth';

export const loadAuthState = (): AuthState | null => {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    const parsed = JSON.parse(raw) as AuthState;
    if (!parsed.accessToken || !parsed.role) {
      localStorage.removeItem(AUTH_STORAGE_KEY);
      return null;
    }

    return {
      accessToken: parsed.accessToken,
      refreshToken: parsed.refreshToken ?? '',
      role: parsed.role,
      email: parsed.email ?? '',
    };
  } catch {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    return null;
  }
};

export const saveAuthState = (state: AuthState): void => {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(state));
};

export const clearAuthState = (): void => {
  localStorage.removeItem(AUTH_STORAGE_KEY);
};

export const getAccessToken = (): string | null => {
  return loadAuthState()?.accessToken ?? null;
};

export const getAuthHeader = (): Record<string, string> => {
  const token = getAccessToken();
  if (!token) {
    return {};
  }

  return {
    Authorization: `Bearer ${token}`,
  };
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
