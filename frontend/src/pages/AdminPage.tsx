import { useEffect, useState } from 'react';
import axios from 'axios';
import { Card } from '../components/Card';
import { api, endpoints } from '../lib/api';

type AdminSummary = {
  usersCount?: number;
  pendingApprovals?: number;
  activeSubscriptions?: number;
};

export const AdminPage = () => {
  const [summary, setSummary] = useState<AdminSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadSummary = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await api.get<AdminSummary | { data: AdminSummary }>(endpoints.adminHealth);
      const payload = 'data' in response.data ? response.data.data : response.data;
      setSummary(payload);
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
      <Card title='Admin secure endpoint check'>
        <button type='button' onClick={() => void loadSummary()} disabled={loading}>
          {loading ? 'Loading...' : 'Reload summary'}
        </button>
        {error && <p className='error-text'>{error}</p>}
        {summary && (
          <ul>
            <li>Users: {summary.usersCount ?? 0}</li>
            <li>Pending approvals: {summary.pendingApprovals ?? 0}</li>
            <li>Active subscriptions: {summary.activeSubscriptions ?? 0}</li>
          </ul>
        )}
      </Card>

      <Card title='Verification + Approval'>Approve or reject company and bursary submissions.</Card>
      <Card title='Users + Audit Logs'>Manage users, roles, and review tracked system actions.</Card>
    </div>
  );
};
