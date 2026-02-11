export type TokenResponse = {
  accessToken: string;
  refreshToken?: string;
};

export type AuthState = {
  token: string;
  role: 'STUDENT' | 'ADMIN' | 'COMPANY';
  email: string;
};

export type JwtPayload = {
  role?: string;
  sub?: string;
  userId?: number;
  exp?: number;
};

export type MeResponse = {
  id: number;
  email: string;
  fullName: string;
  role: 'STUDENT' | 'ADMIN' | 'COMPANY';
};
