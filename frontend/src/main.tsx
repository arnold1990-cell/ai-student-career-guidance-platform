import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Link, Route, Routes } from 'react-router-dom';
import './styles.css';

const Card = ({title,children}:{title:string,children:React.ReactNode}) => <div className='card'><h3>{title}</h3>{children}</div>;
const Layout = ({children}:{children:React.ReactNode}) => <div><header><h1>EduRite</h1><nav>{['/','/student','/company','/admin'].map(p=><Link key={p} to={p}>{p==='/'?'Auth/Landing':p.slice(1)}</Link>)}</nav></header><main>{children}</main></div>;
const Landing=()=> <Layout><Card title='Login/Register'><p>Modern SaaS starter UI for Student, Company, Admin modules.</p></Card></Layout>;
const Student=()=> <Layout><div className='grid'>
<Card title='Dashboard'>Application progress, saved opportunities, analytics.</Card>
<Card title='Profile'>Personal details, qualifications, experience, CV/transcript uploads.</Card>
<Card title='Careers & AI'>Search careers and get AI recommendations.</Card>
<Card title='Bursaries'>Browse, bookmark, apply, and view saved list.</Card>
<Card title='Subscription'>Choose BASIC/PREMIUM and checkout flow.</Card>
<Card title='Notifications'>In-app alert center.</Card>
</div></Layout>;
const Company=()=> <Layout><div className='grid'>
<Card title='Dashboard'>Status, applicants, views, completion rate.</Card>
<Card title='Profile & Verification'>Business details and verification status.</Card>
<Card title='Bursary Management'>Create/edit/submit bursaries for approval.</Card>
<Card title='Talent Search + Shortlist'>Filter students and shortlist candidates.</Card>
<Card title='Messaging'>Message shortlisted students securely.</Card>
</div></Layout>;
const Admin=()=> <Layout><div className='grid'>
<Card title='Dashboard'>Analytics tiles and pending queues.</Card>
<Card title='Users + Bulk Upload'>Manage users and import CSV.</Card>
<Card title='Verification + Approval'>Approve/reject companies and bursaries.</Card>
<Card title='Subscriptions + Payments'>Track status and transaction oversight.</Card>
<Card title='Templates + Audit Logs'>Manage templates and review audit logs.</Card>
</div></Layout>;

ReactDOM.createRoot(document.getElementById('root')!).render(<React.StrictMode><BrowserRouter><Routes><Route path='/' element={<Landing/>}/><Route path='/student' element={<Student/>}/><Route path='/company' element={<Company/>}/><Route path='/admin' element={<Admin/>}/></Routes></BrowserRouter></React.StrictMode>);
