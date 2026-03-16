-- V9__alter_user_role_nullable.sql
-- 신규 유저는 역할 미설정 상태(role=null)로 가입하므로 nullable로 변경

ALTER TABLE "user" ALTER COLUMN role DROP NOT NULL;
