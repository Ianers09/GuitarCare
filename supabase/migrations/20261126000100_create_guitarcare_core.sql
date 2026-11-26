-- GuitarCare Increment 3 core records. Every row is scoped to its owning app user.
create table if not exists public.guitars (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.app_users(id) on delete cascade,
  brand varchar(100) not null,
  model varchar(120) not null,
  type varchar(40) not null,
  purchase_date date,
  photo_url varchar(1000),
  created_at timestamptz not null default now()
);
create index if not exists idx_guitars_user_created on public.guitars(user_id, created_at desc);

create table if not exists public.maintenance_records (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.app_users(id) on delete cascade,
  guitar_id uuid not null references public.guitars(id) on delete cascade,
  activity_type varchar(80) not null,
  activity_date date not null,
  notes varchar(2000),
  photo_url varchar(1000),
  created_at timestamptz not null default now()
);
create index if not exists idx_maintenance_user_guitar_date on public.maintenance_records(user_id, guitar_id, activity_date desc);

create table if not exists public.maintenance_schedules (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.app_users(id) on delete cascade,
  guitar_id uuid not null references public.guitars(id) on delete cascade,
  activity_type varchar(80) not null,
  due_date date not null,
  notes varchar(1000),
  status varchar(20) not null default 'UPCOMING' check (status in ('UPCOMING','COMPLETED','CANCELLED')),
  created_at timestamptz not null default now()
);
create index if not exists idx_schedule_user_due on public.maintenance_schedules(user_id, due_date) where status='UPCOMING';
