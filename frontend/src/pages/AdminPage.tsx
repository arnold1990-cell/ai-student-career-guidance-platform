import { useEffect, useState } from 'react';
import axios from 'axios';
import { Card } from '../components/Card';
import { api, endpoints } from '../lib/api';

type AdminSummary = {
  users?: number;
  bursaries?: number;
};

export const AdminPage = () => {
  const [summary, setSummary] = useState<AdminSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadSummary = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await api.get<{ data: AdminSummary }>(endpoints.adminDashboard);
      setSummary(response.data.data);
    } catch (loadError) {
      if (axios.isAxiosError(loadError)) {
        setError(loadError.response?.data?.message ?? 'Could not load admin summary.');
      } else {
        setError('Could not load admin summary.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadSummary();
  }, []);

  return (
    <div className='grid'>
      <Card title='Admin dashboard'>
        <button type='button' onClick={() => void loadSummary()} disabled={loading}>
          {loading ? 'Loading...' : 'Reload summary'}
        </button>
        {error && <p className='error-text'>{error}</p>}
        {summary && (
          <ul>
            <li>Users: {summary.users ?? 0}</li>
            <li>Bursaries: {summary.bursaries ?? 0}</li>
          </ul>
        )}
      </Card>
    </div>
  );
};
