export type Role = 'STUDENT' | 'COMPANY' | 'ADMIN';

export interface AuthUser {
  id: string;
  email: string;
  role: Role;
}

export interface AuthState {
  token: string;
  user: AuthUser;
}

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';

export const saveAuth = (token: string, user: AuthUser) => {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
};

export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

export const getAuthState = (): AuthState | null => {
  const token = getToken();
  const userRaw = localStorage.getItem(USER_KEY);
  if (!token || !userRaw) {
    return null;
  }

  try {
    const user = JSON.parse(userRaw) as AuthUser;
    return { token, user };
  } catch {
    clearAuth();
    return null;
  }
};

export const clearAuth = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
};
