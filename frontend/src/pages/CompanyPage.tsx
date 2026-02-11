import { Card } from '../components/Card';
import { Layout } from '../components/Layout';
import type { AuthState } from '../types';

export const CompanyPage = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => (
  <Layout auth={auth} onLogout={onLogout}>
    <div className='grid'>
      <Card title='Dashboard'>Status, applicants, views, completion rate.</Card>
      <Card title='Profile & Verification'>Business details and verification status.</Card>
      <Card title='Bursary Management'>Create/edit/submit bursaries for approval.</Card>
      <Card title='Talent Search + Shortlist'>Filter students and shortlist candidates.</Card>
      <Card title='Messaging'>Message shortlisted students securely.</Card>
    </div>
  </Layout>
);
