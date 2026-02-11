insert into iam_roles (name, created_at, updated_at)
values
    ('ADMIN', now(), now()),
    ('STUDENT', now(), now()),
    ('COMPANY', now(), now())
on conflict (name) do nothing;
