import { useState } from 'react';
import axios from 'axios';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { Card } from '../components/Card';
import { api, endpoints } from '../lib/api';
import type { AuthState } from '../types';

export const RegisterPage = ({ auth }: { auth: AuthState | null }) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState<'STUDENT' | 'COMPANY'>('STUDENT');
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
      await api.post(endpoints.authRegister, {
        email,
        fullName,
        password,
        role,
      });
      navigate('/login', { replace: true });
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setError(err.response?.data?.message ?? 'Registration failed.');
      } else {
        setError('Registration failed due to an unknown error.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className='auth-shell'>
      <Card title='Register'>
        <p>Create your account, then sign in.</p>
        <form className='auth-form' onSubmit={submit}>
          <label>
            Full name
            <input value={fullName} onChange={(event) => setFullName(event.target.value)} required />
          </label>
          <label>
            Email
            <input type='email' value={email} onChange={(event) => setEmail(event.target.value)} required />
          </label>
          <label>
            Password
            <input type='password' value={password} onChange={(event) => setPassword(event.target.value)} required />
          </label>
          <label>
            Role
            <select value={role} onChange={(event) => setRole(event.target.value as 'STUDENT' | 'COMPANY')}>
              <option value='STUDENT'>Student</option>
              <option value='COMPANY'>Company</option>
            </select>
          </label>
          {error && <p className='error-text'>{error}</p>}
          <button disabled={submitting} type='submit'>
            {submitting ? 'Creating account...' : 'Create account'}
          </button>
        </form>
        <p className='helper-text'>
          Already have an account? <Link to='/login'>Sign in</Link>
        </p>
      </Card>
    </main>
  );
};
