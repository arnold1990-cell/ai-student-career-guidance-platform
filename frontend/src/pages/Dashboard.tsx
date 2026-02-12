import { useEffect, useState } from 'react';
import api from '../api/axios';
import { clearAuth, type AuthState } from '../auth/authStorage';

interface Props {
  auth: AuthState;
  onLogout: () => void;
}

export const Dashboard = ({ auth, onLogout }: Props) => {
  const [meResult, setMeResult] = useState('');
  const [pingResult, setPingResult] = useState('');

  useEffect(() => {
    const fetchMe = async () => {
      try {
        const response = await api.get('/api/me');
        setMeResult(JSON.stringify(response.data, null, 2));
      } catch (err: any) {
        setMeResult(`Error: ${err.response?.status} ${err.response?.data?.message || ''}`);
      }
    };

    fetchMe();
  }, []);

  const callPing = async (path: string) => {
    try {
      const response = await api.get(path);
      setPingResult(`${path}: ${JSON.stringify(response.data)}`);
    } catch (err: any) {
      setPingResult(`${path}: Error ${err.response?.status} ${err.response?.data?.message || ''}`);
    }
  };

  const logout = () => {
    clearAuth();
    onLogout();
  };

  return (
    <div className='page'>
      <div className='card wide'>
        <h2>Dashboard</h2>
        <p><strong>User:</strong> {auth.user.email} ({auth.user.role})</p>
        <div className='row'>
          <button onClick={() => callPing('/api/student/ping')}>Student Ping</button>
          <button onClick={() => callPing('/api/company/ping')}>Company Ping</button>
          <button onClick={() => callPing('/api/admin/ping')}>Admin Ping</button>
          <button className='secondary' onClick={logout}>Logout</button>
        </div>
        <h3>/api/me result</h3>
        <pre>{meResult || 'Loading...'}</pre>
        <h3>Ping result</h3>
        <pre>{pingResult || 'Click a ping button.'}</pre>
      </div>
    </div>
  );
};
