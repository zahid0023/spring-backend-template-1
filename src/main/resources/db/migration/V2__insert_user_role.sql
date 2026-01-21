insert into roles(name, created_by, updated_by)
values ('USER', 1, 1)
on conflict do nothing;