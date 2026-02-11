import { useState } from 'react';
import axios from 'axios';
import { Navigate, useNavigate } from 'react-router-dom';
import { Card } from '../components/Card';
import { endpoints, api } from '../lib/api';
import { parseJwtPayload, saveAuthState } from '../lib/auth';
import type { AuthState, TokenResponse } from '../types';

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
      const response = await api.post<{ data?: TokenResponse } | TokenResponse>(endpoints.authLogin, {
        email,
        password,
      });

      const responseBody = response.data as { data?: TokenResponse } | TokenResponse;
      const tokenPayload = 'accessToken' in responseBody ? responseBody : responseBody.data;
      if (!tokenPayload?.accessToken) {
        setError('Unexpected login response from server.');
        return;
      }

      const decodedToken = parseJwtPayload(tokenPayload.accessToken);
      const resolvedRole = decodedToken?.role?.toUpperCase();
      const resolvedEmail = decodedToken?.sub ?? email;
      if (!resolvedRole) {
        setError('Unexpected login response from server. Missing role information.');
        return;
      }

      const nextAuth: AuthState = {
        accessToken: tokenPayload.accessToken,
        refreshToken: tokenPayload.refreshToken ?? '',
        role: resolvedRole,
        email: resolvedEmail,
      };

      saveAuthState(nextAuth);
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
