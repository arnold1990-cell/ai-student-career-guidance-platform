import { useState } from 'react';
import axios from 'axios';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { Card } from '../components/Card';
import { api, endpoints } from '../lib/api';
import { getUserFromToken, saveToken } from '../lib/auth';
import type { AuthState, MeResponse, TokenResponse } from '../types';

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

      const responseBody = response.data;
      const tokenPayload = 'accessToken' in responseBody ? responseBody : responseBody.data;
      if (!tokenPayload?.accessToken) {
        setError('Unexpected login response from server.');
        return;
      }

      saveToken(tokenPayload.accessToken);

      const meResponse = await api.get<{ data: MeResponse } | MeResponse>(endpoints.authMe);
      const meData = 'data' in meResponse.data ? meResponse.data.data : meResponse.data;

      const authFromToken = getUserFromToken();
      if (!authFromToken) {
        setError('Received an invalid token from server.');
        return;
      }

      const nextAuth: AuthState = {
        token: tokenPayload.accessToken,
        role: meData.role,
        email: meData.email,
      };

      onLogin(nextAuth);
      navigate(`/${nextAuth.role.toLowerCase()}`, { replace: true });
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
          {error && <p className='error-text'>{error}</p>}
          <button disabled={submitting} type='submit'>
            {submitting ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
        <p className='helper-text'>
          New here? <Link to='/register'>Create an account</Link>
        </p>
      </Card>
    </main>
  );
};
