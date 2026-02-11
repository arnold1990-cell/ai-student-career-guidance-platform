import { Card } from '../components/Card';
import { Layout } from '../components/Layout';
import type { AuthState } from '../types';

export const StudentPage = ({ auth, onLogout }: { auth: AuthState; onLogout: () => void }) => (
  <Layout auth={auth} onLogout={onLogout}>
    <div className='grid'>
      <Card title='Dashboard'>Application progress, saved opportunities, analytics.</Card>
      <Card title='Profile'>Personal details, qualifications, experience, CV/transcript uploads.</Card>
      <Card title='Careers & AI'>Search careers and get AI recommendations.</Card>
      <Card title='Bursaries'>Browse, bookmark, apply, and view saved list.</Card>
      <Card title='Subscription'>Choose BASIC/PREMIUM and checkout flow.</Card>
      <Card title='Notifications'>In-app alert center.</Card>
    </div>
  </Layout>
);
