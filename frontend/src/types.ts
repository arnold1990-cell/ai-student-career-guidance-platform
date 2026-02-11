export type TokenResponse = {
  accessToken: string;
  refreshToken?: string;
};

export type AuthState = {
  accessToken: string;
  refreshToken: string;
  role: string;
  email: string;
};

export type JwtPayload = {
  role?: string;
  sub?: string;
};
