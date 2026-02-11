import { useState } from 'react';
import axios from 'axios';
import { Navigate, useNavigate } from 'react-router-dom';
import { Card } from '../components/Card';
import { AUTH_STORAGE_KEY, parseJwtPayload } from '../lib/auth';
import type { AuthState, TokenResponse } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const LoginPage = ({
  auth,
  onLogin,
}: {
  auth: AuthState | null;
  onLogin: (state: AuthState) => void;
}) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('student@edutech.local');
  const [password, setPassword] = useState('password123');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (auth) {
    return <Navigate to={`/${auth.role.toLowerCase()}`} replace />;
  }

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const response = await axios.post<{ data: TokenResponse }>(`${API_BASE_URL}/api/v1/auth/login`, {
        email,
        password,
      });

      const tokenPayload = response.data.data;
      if (!tokenPayload?.accessToken || !tokenPayload?.refreshToken) {
        setError('Unexpected login response from server.');
        return;
      }

      const decodedToken = parseJwtPayload(tokenPayload.accessToken);
      const resolvedRole = decodedToken?.role?.toUpperCase();
      if (!resolvedRole) {
        setError('Unexpected login response from server. Missing role information.');
        return;
      }

      const nextAuth: AuthState = {
        accessToken: tokenPayload.accessToken,
        refreshToken: tokenPayload.refreshToken,
        role: resolvedRole,
        email,
      };

      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(nextAuth));
      onLogin(nextAuth);
      navigate(`/${resolvedRole.toLowerCase()}`, { replace: true });
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setError(err.response?.data?.message ?? 'Login failed. Please check your credentials.');
      } else {
        setError('Login failed due to an unknown error.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className='auth-shell'>
      <Card title='Login'>
        <p>Sign in to open your dashboard pages.</p>
        <form className='auth-form' onSubmit={submit}>
          <label>
            Email
            <input value={email} onChange={(event) => setEmail(event.target.value)} type='email' required />
          </label>
          <label>
            Password
            <input
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              type='password'
              required
            />
          </label>
          <p className='helper-text'>
            Use seeded accounts such as <strong>student@edutech.local</strong>,{' '}
            <strong>company@edutech.local</strong>, or <strong>admin@edutech.local</strong> with password{' '}
            <strong>password123</strong>.
          </p>
          {error && <p className='error-text'>{error}</p>}
          <button disabled={submitting} type='submit'>
            {submitting ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
      </Card>
    </main>
  );
};
