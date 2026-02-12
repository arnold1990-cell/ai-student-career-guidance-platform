import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';

type RegisterRole = 'STUDENT' | 'COMPANY' | 'ADMIN';

export const Register = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState<RegisterRole>('STUDENT');
  const [invitationCode, setInvitationCode] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    setSuccess('');

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (role !== 'STUDENT' && invitationCode.trim().length === 0) {
      setError(`Invitation code is required for ${role} registration`);
      return;
    }

    setLoading(true);
    try {
      await api.post('/api/auth/register', {
        email,
        password,
        role,
        invitationCode: role === 'STUDENT' ? undefined : invitationCode,
      });
      setSuccess('Registration successful. Please login.');
      setTimeout(() => navigate('/login'), 800);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className='page'>
      <form className='card' onSubmit={onSubmit}>
        <h2>Register</h2>
        <input placeholder='Email' type='email' value={email} onChange={(e) => setEmail(e.target.value)} required />
        <input placeholder='Password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} required minLength={8} />
        <input placeholder='Confirm Password' type='password' value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required minLength={8} />
        <select value={role} onChange={(e) => setRole(e.target.value as RegisterRole)}>
          <option value='STUDENT'>STUDENT</option>
          <option value='COMPANY'>COMPANY</option>
          <option value='ADMIN'>ADMIN</option>
        </select>
        {role !== 'STUDENT' && (
          <input
            placeholder={`${role} invitation code`}
            type='password'
            value={invitationCode}
            onChange={(e) => setInvitationCode(e.target.value)}
            required
          />
        )}
        {error && <p className='error'>{error}</p>}
        {success && <p className='success'>{success}</p>}
        <button type='submit' disabled={loading}>{loading ? 'Creating...' : 'Register'}</button>
        <p>Already have an account? <Link to='/login'>Login</Link></p>
      </form>
    </div>
  );
};
