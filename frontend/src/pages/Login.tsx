import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { saveAuth, type AuthState } from '../auth/authStorage';

interface Props {
  onLogin: (auth: AuthState) => void;
}

export const Login = ({ onLogin }: Props) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/api/auth/login', { email, password });
      const data = response.data;
      saveAuth(data.accessToken, data.user);
      onLogin({ token: data.accessToken, user: data.user });
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className='page'>
      <form className='card' onSubmit={onSubmit}>
        <h2>Login</h2>
        <input placeholder='Email' type='email' value={email} onChange={(e) => setEmail(e.target.value)} required />
        <input placeholder='Password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} required />
        {error && <p className='error'>{error}</p>}
        <button type='submit' disabled={loading}>{loading ? 'Signing in...' : 'Login'}</button>
        <p>New user? <Link to='/register'>Register</Link></p>
      </form>
    </div>
  );
};
