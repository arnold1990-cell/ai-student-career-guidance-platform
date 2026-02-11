create table iam_roles (id bigserial primary key, name varchar(50) unique not null, created_at timestamp not null default now(), updated_at timestamp not null default now());
create table iam_users (id bigserial primary key, email varchar(255) unique not null, password_hash varchar(255) not null, full_name varchar(255) not null, status varchar(50) not null, role_id bigint not null references iam_roles(id), refresh_token varchar(255), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table iam_password_reset_tokens (id bigserial primary key, token varchar(255) unique not null, user_id bigint not null references iam_users(id), expires_at timestamp not null, used boolean not null, created_at timestamp not null default now(), updated_at timestamp not null default now());
create table iam_audit_logins (id bigserial primary key, email varchar(255), success boolean, ip_address varchar(100), created_at timestamp not null default now(), updated_at timestamp not null default now());

create table student_profiles (id bigserial primary key, user_id bigint not null unique references iam_users(id), interests varchar(255), location_preference varchar(255), bio text, created_at timestamp not null default now(), updated_at timestamp not null default now());
create table student_documents (id bigserial primary key, student_profile_id bigint not null references student_profiles(id), file_name varchar(255), content_type varchar(100), size_bytes bigint, object_key varchar(255), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table student_qualifications (id bigserial primary key, student_profile_id bigint not null references student_profiles(id), institution varchar(255), title varchar(255), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table student_experiences (id bigserial primary key, student_profile_id bigint not null references student_profiles(id), organization varchar(255), role_title varchar(255), created_at timestamp not null default now(), updated_at timestamp not null default now());

create table company_profiles (id bigserial primary key, owner_user_id bigint not null references iam_users(id), company_name varchar(255), status varchar(80), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table company_verification_requests (id bigserial primary key, company_profile_id bigint not null references company_profiles(id), status varchar(80), admin_comment varchar(255), created_at timestamp not null default now(), updated_at timestamp not null default now());

create table bursaries (id bigserial primary key, company_profile_id bigint references company_profiles(id), title varchar(255), description text, approval_status varchar(80), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table bursary_eligibility_criteria (id bigserial primary key, bursary_id bigint unique not null references bursaries(id), minimum_qualification varchar(255), region varchar(255), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table bursary_applications (id bigserial primary key, bursary_id bigint not null references bursaries(id), student_profile_id bigint not null references student_profiles(id), status varchar(80), created_at timestamp not null default now(), updated_at timestamp not null default now());

create table careers (id bigserial primary key, title varchar(255), category varchar(255), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table career_recommendations (id bigserial primary key, student_profile_id bigint references student_profiles(id), career_id bigint references careers(id), reason text, created_at timestamp not null default now(), updated_at timestamp not null default now());

create table talent_shortlists (id bigserial primary key, company_profile_id bigint references company_profiles(id), student_profile_id bigint references student_profiles(id), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table message_threads (id bigserial primary key, company_profile_id bigint, student_profile_id bigint, created_at timestamp not null default now(), updated_at timestamp not null default now());
create table messages (id bigserial primary key, thread_id bigint references message_threads(id), sender_user_id bigint, body text, created_at timestamp not null default now(), updated_at timestamp not null default now());

create table admin_audit_logs (id bigserial primary key, actor_user_id bigint, action varchar(255), payload text, created_at timestamp not null);
create table moderation_queue (id bigserial primary key, item_type varchar(255), item_id bigint, status varchar(80), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table templates (id bigserial primary key, channel varchar(80), template_name varchar(255), content text, created_at timestamp not null default now(), updated_at timestamp not null default now());

create table subscription_plans (id bigserial primary key, code varchar(80) unique, name varchar(255), monthly_price numeric(12,2), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table student_subscriptions (id bigserial primary key, user_id bigint not null references iam_users(id), plan_id bigint not null references subscription_plans(id), status varchar(80), created_at timestamp not null default now(), updated_at timestamp not null default now());
create table payment_transactions (id bigserial primary key, provider_ref varchar(255), status varchar(80), amount numeric(12,2), created_at timestamp not null default now(), updated_at timestamp not null default now());

create table notifications (id bigserial primary key, user_id bigint not null references iam_users(id), title varchar(255), body text, read_status boolean, created_at timestamp not null default now(), updated_at timestamp not null default now());
create table outbox_events (id bigserial primary key, aggregate_type varchar(100), aggregate_id bigint, event_type varchar(120), payload text, status varchar(80), created_at timestamp not null default now(), updated_at timestamp not null default now());
