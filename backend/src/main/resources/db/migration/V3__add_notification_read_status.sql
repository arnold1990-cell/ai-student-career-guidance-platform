alter table if exists notifications
    add column if not exists read_status boolean not null default false;

update notifications
set read_status = true
where upper(coalesce(status, '')) = 'READ';
