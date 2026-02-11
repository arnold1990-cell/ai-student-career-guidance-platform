import { Card } from '../components/Card';
import { Layout } from '../components/Layout';
import type { AuthState } from '../types';

export const AdminPage = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => (
  <Layout auth={auth} onLogout={onLogout}>
    <div className='grid'>
      <Card title='Dashboard'>Analytics tiles and pending queues.</Card>
      <Card title='Users + Bulk Upload'>Manage users and import CSV.</Card>
      <Card title='Verification + Approval'>Approve/reject companies and bursaries.</Card>
      <Card title='Subscriptions + Payments'>Track status and transaction oversight.</Card>
      <Card title='Templates + Audit Logs'>Manage templates and review audit logs.</Card>
    </div>
  </Layout>
);
