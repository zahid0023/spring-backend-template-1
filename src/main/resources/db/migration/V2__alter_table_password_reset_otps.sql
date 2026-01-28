drop table if exists reset_tokens;

alter table password_reset_otps
    add column if not exists reset_token text;